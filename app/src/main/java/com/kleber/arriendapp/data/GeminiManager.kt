package com.kleber.arriendapp.data

import android.util.Log
import com.kleber.arriendapp.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiManager {
    private const val TAG = "GeminiManager"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun chatWithGemini(
        userMessage: String,
        repository: FirebaseRepository,
        onAutoScheduleDetected: suspend (ReservaEntity) -> Unit
    ): String {
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext "Error: No se ha configurado la clave API de Gemini en AI Studio. Revisa tu local.properties."
            }

            try {
                // Leer datos actuales de Firestore
                val inventory = repository.getInventoryStream().firstOrNull() ?: emptyList()
                val claims = repository.getReservationsStream().firstOrNull() ?: emptyList()

                val inventorySummary = inventory.joinToString("\n") { 
                    "- ID: ${it.id}, Nombre: ${it.nombre}, Precio: $${it.precio}, Estado: ${it.estado}"
                }
                val claimsSummary = claims.joinToString("\n") {
                    "- ID Reserva: ${it.id}, EquipoID: ${it.equipmentId}, Cliente: ${it.clienteNombre}, Dirección: ${it.clienteDireccion}, Fecha/Hora: ${it.fechaHora}, Estado: ${it.estado}"
                }

                val systemPrompt = """
                    Eres Arrie, el asistente inteligente de la app 'ArriendApp' en español. Tus usuarios son trabajadores del servicio de arriendos.
                    Tienes acceso inmediato a los datos en tiempo real de Firestore:
                    
                    INVENTARIO ACTUAL:
                    $inventorySummary
                    
                    RESERVAS ACTUALES:
                    $claimsSummary
                    
                    Instrucciones importantes:
                    1. Responde preguntas breves en un tono amigable, profesional y directo.
                    2. Si el usuario te pide agendar un equipo (por ejemplo: "Agenda un Castillo Real para Juan Pérez en Av Libertad 4500 hoy a las 14:00 hs"), ayuda al operador extrayendo los datos.
                    3. Al agendar, asocia el ID del equipo correcto buscando una coincidencia de nombre en el inventario actual (ej. id: castillo_real).
                    4. Si toda la información requerida está disponible para agendar, debes responder amablemente confirmando la reserva y de forma OBLIGATORIA incluir al final exactamente una línea con este formato JSON para nuestra automatización:
                       [REGISTRAR_RESERVA_JSON]: {"cliente":"NOMBRE", "direccion":"DIRECCION_COMPLETA", "contacto":"TELEFONO", "id_equipo":"ID_CORRECTO", "monto": PRECIO_EQUIPO, "fecha_hora":"HH:MM"}
                    5. Sé preciso con las horas o direcciones. ¡Gracias!
                """.trimIndent()

                val requestBody = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = userMessage)))
                    ),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
                )

                val response = api.generateContent(apiKey, requestBody)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Lo siento, no obtuve respuesta del asistente."

                // Check for auto-scheduling payload
                if (responseText.contains("[REGISTRAR_RESERVA_JSON]:")) {
                    try {
                        val jsonLine = responseText.substringAfter("[REGISTRAR_RESERVA_JSON]:").trim()
                        val jsonObj = JSONObject(jsonLine)
                        val cliente = jsonObj.getString("cliente")
                        val direccion = jsonObj.getString("direccion")
                        val idEquipo = jsonObj.getString("id_equipo")
                        val fechaHora = jsonObj.getString("fecha_hora")
                        val monto = jsonObj.optDouble("monto", 15000.0)
                        val contacto = jsonObj.optString("contacto", "No especificado")

                        val currentUser = repository.getCurrentUserEntity()
                        val workerId = currentUser?.uid ?: "AI_ASSISTANT"

                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaRegistro = dateFormat.format(Date())

                        val newReserva = ReservaEntity(
                            id = UUID.randomUUID().toString(),
                            equipmentId = idEquipo,
                            fechaHora = fechaHora,
                            fechaRegistro = fechaRegistro,
                            clienteNombre = cliente,
                            clienteDireccion = direccion,
                            clienteContacto = contacto,
                            monto = monto,
                            estado = "Pendiente",
                            workerId = workerId
                        )

                        // Ejecutar callback para guardar la reserva
                        onAutoScheduleDetected(newReserva)

                    } catch (e: Exception) {
                        Log.e(TAG, "Error autocompleting reservation from Gemini: ${e.message}")
                    }
                }

                // Devolver el texto limpio sin el JSON
                responseText.substringBefore("[REGISTRAR_RESERVA_JSON]:").trim()

            } catch (e: Exception) {
                Log.e(TAG, "Error call to Gemini API: ${e.message}")
                "Error al procesar la solicitud con Gemini: ${e.localizedMessage ?: e.message}"
            }
        }
    }
}
