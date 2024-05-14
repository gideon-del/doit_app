package com.example.dooit.ui.doitviewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState= _uiState.asStateFlow()
    fun toggleShowAll(showAll: Boolean) {
        _uiState.update {
            it.copy(
                showAll = showAll
            )
        }
    }
}

data class HomeUiState(
    val showAll: Boolean = false,

    )