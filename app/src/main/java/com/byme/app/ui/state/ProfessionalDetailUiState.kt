package com.byme.app.ui.state

import com.byme.app.domain.model.Review
import com.byme.app.domain.model.User

data class ProfessionalDetailUiState(
    val isLoading: Boolean = false,
    val professional: User? = null,
    val reviews: List<Review> = emptyList(),
    val errorMessage: String? = null,
    val selectedTab: Int = 0,
)