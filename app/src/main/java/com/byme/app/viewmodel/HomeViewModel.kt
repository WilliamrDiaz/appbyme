package com.byme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.data.remote.repository.ProfessionalRepository
import com.byme.app.domain.usecase.GetProfessionalsUseCase
import com.byme.app.domain.usecase.SearchProfessionalsUseCase
import com.byme.app.model.Professional
import com.byme.app.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfessionalsUseCase: GetProfessionalsUseCase,
    private val searchProfessionalsUseCase: SearchProfessionalsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadProfessionals()
    }

    fun loadProfessionals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getProfessionalsUseCase().fold(
                onSuccess = { professionals ->
                    _uiState.update {
                        it.copy(isLoading = false, professionals = professionals)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar profesionales"
                        )
                    }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadProfessionals()
            } else {
                _uiState.update { it.copy(isLoading = true) }
                searchProfessionalsUseCase(query).fold(
                    onSuccess = { professionals ->
                        _uiState.update {
                            it.copy(isLoading = false, professionals = professionals)
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Error en la búsqueda"
                            )
                        }
                    }
                )
            }
        }
    }
}