package com.kleber.arriendapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Auth
    val currentUser = auth.currentUser

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error loginUser: ${e.message}", e)
            false
        }
    }

    suspend fun registerUser(email: String, password: String, nombre: String, rol: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val userEntity = UserEntity(uid = user.uid, nombre = nombre, rol = rol)
                firestore.collection("usuarios").document(user.uid).set(userEntity).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error registerUser: ${e.message}", e)
            false
        }
    }

    suspend fun loginWithGoogle(idToken: String): Boolean {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                // Crear documento base si es su primer inicio de sesión
                val doc = firestore.collection("usuarios").document(user.uid).get().await()
                if (!doc.exists()) {
                    val userEntity = UserEntity(uid = user.uid, nombre = user.displayName ?: "Usuario Google", rol = "Operador Logística")
                    firestore.collection("usuarios").document(user.uid).set(userEntity).await()
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error loginWithGoogle: ${e.message}", e)
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUserEntity(): UserEntity? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = firestore.collection("usuarios").document(uid).get().await()
            if (doc.exists()) {
                UserEntity(
                    uid = doc.id,
                    nombre = doc.getString("nombre") ?: "",
                    rol = doc.getString("rol") ?: ""
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Firestore - Inventario
    fun getInventoryStream(): Flow<List<InventoryEntity>> = callbackFlow {
        val listener = firestore.collection("inventario").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    InventoryEntity(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        imagen = doc.getString("imagen") ?: "",
                        precio = doc.getDouble("precio") ?: 0.0,
                        estado = doc.getString("estado") ?: "DISPONIBLE"
                    )
                }
                trySend(items)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveInventoryItem(item: InventoryEntity) {
        firestore.collection("inventario").document(item.id).set(item).await()
    }

    suspend fun deleteInventoryItem(id: String) {
        firestore.collection("inventario").document(id).delete().await()
    }

    suspend fun updateInventoryStatus(id: String, estado: String) {
        firestore.collection("inventario").document(id).update("estado", estado).await()
    }

    suspend fun getUserNameById(uid: String): String {
        return try {
            val doc = firestore.collection("usuarios").document(uid).get().await()
            if (doc.exists()) {
                doc.getString("nombre") ?: uid
            } else uid
        } catch (e: Exception) {
            uid
        }
    }

    // Firestore - Reservas
    fun getReservationsStream(): Flow<List<ReservaEntity>> = callbackFlow {
        val listener = firestore.collection("reservas").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    ReservaEntity(
                        id = doc.id,
                        equipmentId = doc.getString("equipmentId") ?: "",
                        fechaHora = doc.getString("fechaHora") ?: "",
                        fechaRegistro = doc.getString("fechaRegistro") ?: "",
                        clienteNombre = doc.getString("clienteNombre") ?: "",
                        clienteDireccion = doc.getString("clienteDireccion") ?: "",
                        clienteContacto = doc.getString("clienteContacto") ?: "",
                        monto = doc.getDouble("monto") ?: 0.0,
                        estado = doc.getString("estado") ?: "Pendiente",
                        workerId = doc.getString("workerId") ?: ""
                    )
                }
                trySend(items)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveReserva(reserva: ReservaEntity) {
        firestore.collection("reservas").document(reserva.id).set(reserva).await()
    }

    suspend fun updateReservaStatus(id: String, estado: String) {
        firestore.collection("reservas").document(id).update("estado", estado).await()
    }
}
