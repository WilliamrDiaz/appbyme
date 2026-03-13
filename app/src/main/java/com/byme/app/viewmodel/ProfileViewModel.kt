package com.byme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.domain.usecase.GetUserUseCase
import com.byme.app.domain.usecase.UpdateUserUseCase
import com.byme.app.ui.state.ProfileUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUserUseCase(userId).fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            name = user.name,
                            lastname = user.lastname,
                            phone = user.phone
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onLastnameChange(value: String) = _uiState.update { it.copy(lastname = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }

    fun saveProfile() {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val updatedUser = user.copy(
                name = _uiState.value.name,
                lastname = _uiState.value.lastname,
                phone = _uiState.value.phone
            )
            updateUserUseCase(updatedUser).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
}