package com.byme.app.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.domain.usecase.LoginUseCase
import com.byme.app.domain.usecase.RegisterUseCase
import com.byme.app.ui.state.AuthUiState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    val currentUser: FirebaseUser? get() = auth.currentUser

    // Email y contraseña - Login
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = loginUseCase(email, password)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al iniciar sesión"
                        )
                    }
                }
            )
        }
    }

    // Email y contraseña - Registro
    fun registerWithEmail(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = registerUseCase(name, lastname, email, phone, password)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al registrarse"
                        )
                    }
                }
            )
        }
    }

    // Google Sign-In
    fun loginWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(context, request)
                val googleIdToken = GoogleIdTokenCredential
                    .createFrom(result.credential.data).idToken
                val firebaseCredential = GoogleAuthProvider
                    .getCredential(googleIdToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener {
                        _uiState.update {
                            it.copy(isLoading = false, isSuccess = true)
                        }
                    }
                    .addOnFailureListener { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Error con Google"
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error con Google"
                    )
                }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _uiState.update { AuthUiState() }
    }

    fun resetState() {
        _uiState.update { it.copy(isSuccess = false, errorMessage = null) }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}