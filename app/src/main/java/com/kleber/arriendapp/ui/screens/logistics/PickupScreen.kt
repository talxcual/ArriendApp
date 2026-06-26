package com.kleber.arriendapp.ui.screens.logistics

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kleber.arriendapp.ui.theme.Fondo
import com.kleber.arriendapp.ui.theme.Primario
import com.kleber.arriendapp.ui.theme.Secundario
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupScreen(
    reservaId: String,
    onBackPressed: () -> Unit,
    viewModel: LogisticsViewModel = viewModel()
) {
    val context = LocalContext.current
    val reservas by viewModel.reservas.collectAsState()
    val reserva = reservas.find { it.id == reservaId }

    if (reserva == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Reserva no encontrada.", color = Color.Gray)
            Button(onClick = onBackPressed) { Text("Volver") }
        }
        return
    }

    var check1 by remember { mutableStateOf(false) }
    var check2 by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Retiro") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Primario,
                    navigationIconContentColor = Primario
                )
            )
        },
        containerColor = Fondo
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cliente", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                    Text(reserva.clienteNombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = Primario)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dirección", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                    Text(reserva.clienteDireccion, style = MaterialTheme.typography.bodyLarge, color = Primario)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Checklist de Estado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primario)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = check1, onCheckedChange = { check1 = it }, colors = CheckboxDefaults.colors(checkedColor = Secundario))
                Text("Equipo completo (sin faltantes)", color = Primario)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = check2, onCheckedChange = { check2 = it }, colors = CheckboxDefaults.colors(checkedColor = Secundario))
                Text("Equipo limpio y en buen estado", color = Primario)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val encoded = URLEncoder.encode(reserva.clienteDireccion, "UTF-8")
                    val uri = Uri.parse("google.navigation:q=$encoded")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$encoded"))
                            context.startActivity(browserIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No hay navegador disponible", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Secundario),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Viaje", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!check1 || !check2) {
                        Toast.makeText(context, "Debe completar el checklist", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.completePickup(reservaId, reserva.equipmentId)
                    Toast.makeText(context, "Retiro Completado, Equipo DISPONIBLE", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primario),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Completar Retiro", fontWeight = FontWeight.Bold)
            }
        }
    }
}
