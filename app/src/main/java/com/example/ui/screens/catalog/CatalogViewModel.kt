package com.example.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseRepository
import com.example.data.InventoryEntity
import com.example.data.ReservaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

class CatalogViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _inventory = MutableStateFlow<List<InventoryEntity>>(emptyList())
    val inventory: StateFlow<List<InventoryEntity>> = _inventory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getInventoryStream()
                .catch { e ->
                    // Handle error
                }
                .collect { items ->
                    _inventory.value = items
                }
        }
    }

    fun scheduleRental(
        item: InventoryEntity,
        clientName: String,
        clientAddress: String,
        clientPhone: String,
        eventDate: String,
        eventTime: String
    ) {
        viewModelScope.launch {
            val user = repository.getCurrentUserEntity()
            val workerId = user?.uid ?: "UNKNOWN"
            val uidReserva = UUID.randomUUID().toString()
            
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val fechaRegistroStr = dateFormat.format(java.util.Date())

            val newReserva = ReservaEntity(
                id = uidReserva,
                equipmentId = item.id,
                fechaHora = "$eventDate $eventTime",
                fechaRegistro = fechaRegistroStr,
                clienteNombre = clientName,
                clienteDireccion = clientAddress,
                clienteContacto = clientPhone,
                monto = item.precio,
                estado = "Pendiente",
                workerId = workerId
            )
            
            repository.saveReserva(newReserva)
            repository.updateInventoryStatus(item.id, "OCUPADO")
        }
    }

    fun saveItem(id: String?, nombre: String, precio: Double, imagenUrl: String) {
        viewModelScope.launch {
            val finalId = id ?: UUID.randomUUID().toString()
            val newItem = InventoryEntity(
                id = finalId,
                nombre = nombre,
                precio = precio,
                imagen = imagenUrl,
                estado = "DISPONIBLE"
            )
            repository.saveInventoryItem(newItem)
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            repository.deleteInventoryItem(id)
        }
    }
}
