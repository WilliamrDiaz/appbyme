package com.byme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.domain.usecase.GetUserUseCase
import com.byme.app.domain.repository.ReviewRepositoryInterface
import com.byme.app.ui.state.ProfessionalDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfessionalDetailViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val reviewRepository: ReviewRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfessionalDetailUiState())
    val uiState: StateFlow<ProfessionalDetailUiState> = _uiState

    fun loadProfessional(professionalId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getUserUseCase(professionalId).fold(
                onSuccess = { professional ->
                    _uiState.update { it.copy(isLoading = false, professional = professional) }
                    loadReviews(professionalId)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar el profesional"
                        )
                    }
                }
            )
        }
    }

    private fun loadReviews(professionalId: String) {
        viewModelScope.launch {
            reviewRepository.getReviews(professionalId).fold(
                onSuccess = { reviews ->
                    _uiState.update { it.copy(reviews = reviews) }
                },
                onFailure = { }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}