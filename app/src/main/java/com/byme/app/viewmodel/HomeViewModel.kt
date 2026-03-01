package com.byme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byme.app.data.remote.repository.ProfessionalRepository
import com.byme.app.model.Professional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = ProfessionalRepository()

    private val _professionals = MutableStateFlow<List<Professional>>(emptyList())
    val professionals: StateFlow<List<Professional>> = _professionals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadProfessionals()
    }

    fun loadProfessionals() {
        viewModelScope.launch {
            _isLoading.value = true
            _professionals.value = repository.getAllProfessionals()
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadProfessionals()
            } else {
                _isLoading.value = true
                _professionals.value = repository.searchProfessionals(query)
                _isLoading.value = false
            }
        }
    }
}