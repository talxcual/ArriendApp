package com.kleber.arriendapp.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kleber.arriendapp.data.FirebaseRepository
import com.kleber.arriendapp.data.InventoryEntity
import com.kleber.arriendapp.data.ReservaEntity
import com.kleber.arriendapp.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CalendarViewModel(
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

    suspend fun getWorkerName(workerId: String): String {
        return repository.getUserNameById(workerId)
    }
}
