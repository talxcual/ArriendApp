package com.example.ui.screens.logistics

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

class LogisticsViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _reservas = MutableStateFlow<List<ReservaEntity>>(emptyList())
    val reservas: StateFlow<List<ReservaEntity>> = _reservas.asStateFlow()

    private val _inventory = MutableStateFlow<List<InventoryEntity>>(emptyList())
    val inventory: StateFlow<List<InventoryEntity>> = _inventory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getReservationsStream()
                .catch { /* error handling */ }
                .collect { items ->
                    _reservas.value = items
                }
        }
        viewModelScope.launch {
            repository.getInventoryStream()
                .catch { /* error handling */ }
                .collect { items ->
                    _inventory.value = items
                }
        }
    }

    fun completeDelivery(reservaId: String) {
        viewModelScope.launch {
            repository.updateReservaStatus(reservaId, "Entregado")
        }
    }

    fun completePickup(reservaId: String, equipmentId: String) {
        viewModelScope.launch {
            repository.updateReservaStatus(reservaId, "Finalizado")
            repository.updateInventoryStatus(equipmentId, "DISPONIBLE")
        }
    }
}
