package com.byme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.domain.repository.UserRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserTypeViewModel @Inject constructor(
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _isProfessional = MutableStateFlow<Boolean?>(null)
    val isProfessional: StateFlow<Boolean?> = _isProfessional

    init {
        checkUserType()
    }

    private fun checkUserType() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.getUser(userId).fold(
                onSuccess = { user ->
                    _isProfessional.update { user.isProfessional }
                },
                onFailure = {
                    _isProfessional.update { false }
                }
            )
        }
    }
}