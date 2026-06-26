package com.kleber.arriendapp.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kleber.arriendapp.data.FirebaseRepository
import com.kleber.arriendapp.data.GeminiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

class GeminiViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        // Añadir mensaje del usuario
        _messages.value = _messages.value + ChatMessage(userText, isFromUser = true)
        _isLoading.value = true

        viewModelScope.launch {
            val responseText = GeminiManager.chatWithGemini(
                userMessage = userText,
                repository = repository,
                onAutoScheduleDetected = { newReserva ->
                    // Guardar la reserva generada por la IA
                    repository.saveReserva(newReserva)
                    // Actualizar el estado del equipo
                    repository.updateInventoryStatus(newReserva.equipmentId, "OCUPADO")
                }
            )

            _messages.value = _messages.value + ChatMessage(responseText, isFromUser = false)
            _isLoading.value = false
        }
    }
}
