package com.byme.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.data.local.DraftManager
import com.byme.app.domain.model.Chat
import com.byme.app.domain.repository.ChatRepositoryInterface
import com.byme.app.ui.state.ChatListUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepositoryInterface,
    private val savedStateHandle: SavedStateHandle,
    private val draftManager: DraftManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState

    init {
        loadChats()
    }

    private fun loadChats() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                chatRepository.getChats(userId).collect { chats ->
                    // Cargar borradores para cada chat
                    val drafts = draftManager.getAllDrafts()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            chats = chats,
                            drafts = drafts
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun refreshDrafts() {
        val drafts = draftManager.getAllDrafts()
        val existingChatIds = _uiState.value.chats.map { it.id }.toSet()

        // Crear chats pendientes para borradores que no tienen chat en Firestore
        val pendingChats = drafts.keys
            .filter { chatId -> chatId !in existingChatIds }
            .map { chatId ->
                Chat(
                    id = chatId,
                    professionalName = draftManager.getDraftName(chatId),
                    lastMessageTime = 0L
                )
            }

        _uiState.update {
            it.copy(
                drafts = drafts,
                pendingChats = pendingChats
            )
        }
    }
}