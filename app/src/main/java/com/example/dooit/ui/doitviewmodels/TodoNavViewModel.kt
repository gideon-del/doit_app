package com.example.dooit.ui.doitviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dooit.data.AppPreference
import com.example.dooit.data.todoApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoNavViewModel(
   private val  appPreference: AppPreference
):ViewModel() {
    val uiState: StateFlow<Boolean> = appPreference.firstTime.map {
        it
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun changeStatus() {
        viewModelScope.launch {
            appPreference.updateStatus()
        }
    }
    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer{
                TodoNavViewModel(todoApplication().appStatus)
            }
        }
    }
}