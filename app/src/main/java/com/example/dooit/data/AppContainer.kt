package com.example.dooit.data

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.dooit.TodoListApplication


interface TodoRepo {
    fun getAllListItem(): List<TodoItemWithTask>
    fun getListItem(id: Int): TodoItemWithTask
    suspend fun insertTodoList(todoItem: TodoListEntity): Long
    suspend fun insertItem(todoList: TodoItemEntity)
    suspend fun updateTodoList(todoList: TodoListEntity)
    suspend fun updateTodoItem(todoItem: TodoItemEntity)
    suspend fun insertImage(image: ImageEntity): Long
    suspend fun insertAudio(audiEntity: AudiEntity): Long
}

interface AppConainer {
    val todoRepo: TodoRepo
}

class OfflineRepo(private val itemDAO: TodoDAO) : TodoRepo {
    override fun getAllListItem(): List<TodoItemWithTask> = itemDAO.getAllListItems()
    override fun getListItem(id: Int): TodoItemWithTask = itemDAO.getListItem(id)
    override suspend fun insertItem(todoList: TodoItemEntity) = itemDAO.insertItem(todoList)
    override suspend fun insertTodoList(todoItem: TodoListEntity) = itemDAO.insertTodoList(todoItem)
    override suspend fun updateTodoItem(todoItem: TodoItemEntity) = itemDAO.updateTodoItem(todoItem)
    override suspend fun updateTodoList(todoList: TodoListEntity) = itemDAO.updateTodoList(todoList)
    override suspend fun insertImage(image: ImageEntity): Long = itemDAO.insertImage(image)
    override suspend fun insertAudio(audiEntity: AudiEntity): Long = itemDAO.insertAudio(audiEntity)
}

class DefaultContainer(context: Context) : AppConainer {
    override val todoRepo: TodoRepo = OfflineRepo(TodoListDB.getDatabase(context).todoListDAO())
}

fun CreationExtras.todoApplication(): TodoListApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoListApplication
}