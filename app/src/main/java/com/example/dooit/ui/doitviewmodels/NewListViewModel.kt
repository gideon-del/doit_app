package com.example.dooit.ui.doitviewmodels

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dooit.data.TodoItemEntity
import com.example.dooit.data.TodoItemWithTask
import com.example.dooit.data.TodoListEntity
import com.example.dooit.data.TodoRepo
import com.example.dooit.data.todoApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOError

data class ListItemState(
    val id: Int,
    var item: String,

    )
sealed class TodoListUIStates {
    data class Success(val item: TodoItemWithTask): TodoListUIStates()
    object Loading: TodoListUIStates()
    object Error:TodoListUIStates()
}
class NewListViewModel(private val todoRepo: TodoRepo) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoListUIStates>(TodoListUIStates.Loading)
    val uiState = _uiState.asStateFlow()
     fun getList(id:Int) {
         viewModelScope.launch(Dispatchers.IO) {

             _uiState.value = try {
                 val todoList = todoRepo.getListItem(id)
                 TodoListUIStates.Success(todoList)
             } catch (e:IOError){
                 TodoListUIStates.Error
             }
         }

    }
    suspend fun createNewList() {

     _uiState.value =   try {
            val newList = todoRepo.insertTodoList(TodoListEntity(title = "Title", label = ""))

            val newItem = TodoItemEntity(todoItemId = newList.toInt(), task = "To do")
           todoRepo.insertItem(newItem)
         Log.d("Current","New Todo Inserted ${newItem} ${newList}")
         val mainList = todoRepo.getListItem(newList.toInt())

         TodoListUIStates.Success(mainList)
        } catch (e: IOError){
            TodoListUIStates.Error
        }



    }
    fun updateTodoItem(todoItemEntity: TodoItemEntity) {
       viewModelScope.launch {
           withContext(Dispatchers.IO){
               _uiState.value = try {
//                   todoRepo.updateTodoItem(todoItemEntity)
//                   val  updatedList = todoRepo.getListItem((_uiState.value as TodoListUIStates.Success).item.todoList.id)
                   val currentState = (_uiState.value as TodoListUIStates.Success).item
                   TodoListUIStates.Success(currentState.copy(
                       items = currentState.items.map {
                           if(it.id == todoItemEntity.id){
                               todoItemEntity
                           } else{
                               it
                           }
                       }
                   ))
               } catch (e: IOError){
                   TodoListUIStates.Error
               }
               todoRepo.updateTodoItem(todoItemEntity)
           }

       }





    }
    fun updateTodoList(listEntity: TodoListEntity){
        viewModelScope.launch {
            _uiState.value = try {
                val currentList = (_uiState.value as TodoListUIStates.Success).item
                TodoListUIStates.Success(currentList.copy(
                    todoList = listEntity
                ))
            } catch (e: IOError){
                TodoListUIStates.Error
            }
            todoRepo.updateTodoList(listEntity)
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NewListViewModel(todoApplication().container.todoRepo)
            }

        }
    }
}