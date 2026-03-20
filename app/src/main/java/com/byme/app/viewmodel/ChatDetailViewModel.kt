package com.byme.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.data.local.DraftManager
import com.byme.app.domain.model.Message
import com.byme.app.domain.repository.ChatRepositoryInterface
import com.byme.app.domain.repository.UserRepositoryInterface
import com.byme.app.ui.state.ChatDetailUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val savedStateHandle: SavedStateHandle,
    private val draftManager: DraftManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState

    private var professionalId: String = ""
    private var chatExists: Boolean = false

    fun loadChat(chatId: String, professionalName: String) {
        // Extraer professionalId del chatId (formato: userId_professionalId)
        professionalId = chatId.substringAfter("_")

        // Restaurar borrador si existe
        val draft = draftManager.getDraft(chatId)

        _uiState.update {
            it.copy(
                chatId = chatId,
                professionalName = professionalName,
                messageText = draft
            )
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Verificar si el chat ya existe en Firestore
                chatRepository.getMessages(chatId).collect { messages ->
                    chatExists = messages.isNotEmpty()
                    _uiState.update { it.copy(isLoading = false, messages = messages) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, messages = emptyList()) }
            }
        }
    }

    fun onMessageTextChange(text: String) {
        _uiState.update { it.copy(messageText = text) }
        // Guardar borrador
        draftManager.saveDraft(
            _uiState.value.chatId,
            text,
            _uiState.value.professionalName)
    }

    fun sendMessage() {
        val state = _uiState.value
        if (state.messageText.isBlank()) return
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        viewModelScope.launch {
            val message = Message(
                senderId = currentUser.uid,
                text = state.messageText.trim(),
                timestamp = System.currentTimeMillis()
            )

            // Si el chat no existe, crearlo primero
            if (!chatExists) {
                val userId = currentUser.uid
                userRepository.getUser(userId).fold(
                    onSuccess = { currentUserData ->
                        val userName = "${currentUserData.name} ${currentUserData.lastname}"
                        userRepository.getUser(professionalId).fold(
                            onSuccess = { professional ->
                                val professionalName = "${professional.name} ${professional.lastname}"
                                chatRepository.getOrCreateChat(
                                    userId = userId,
                                    professionalId = professionalId,
                                    userName = userName,
                                    professionalName = professionalName
                                )
                                chatExists = true
                            },
                            onFailure = { }
                        )
                    },
                    onFailure = { }
                )
            }

            chatRepository.sendMessage(state.chatId, message)

            // Limpiar borrador
            draftManager.clearDraft(state.chatId)
            _uiState.update { it.copy(messageText = "") }
        }
    }

    fun markAsRead() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            chatRepository.markAsRead(_uiState.value.chatId, userId)
        }
    }

    fun getDraft(chatId: String): String {
        return savedStateHandle.get<String>("draft_$chatId") ?: ""
    }
}