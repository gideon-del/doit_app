package com.example.dooit.ui.doitviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dooit.data.TodoItemWithTask
import com.example.dooit.data.TodoRepo
import com.example.dooit.data.todoApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(todoRepo: TodoRepo) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState(
        todoLists = listOf()    ))
    val uiState= _uiState.asStateFlow()
    fun toggleShowAll(showAll: Boolean) {
        _uiState.update {
            it.copy(
                showAll = showAll
            )
        }
    }
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val allItems =todoRepo.getAllListItem()
            _uiState.value = _uiState.value.copy(
                todoLists = allItems
            )
        }
    }
    companion object  {
       val Factory: ViewModelProvider.Factory = viewModelFactory {
initializer {
HomeScreenViewModel(todoApplication().container.todoRepo)
}
       }
    }
}

data class HomeUiState(
    val showAll: Boolean = true,
    val todoLists: List<TodoItemWithTask>
    )