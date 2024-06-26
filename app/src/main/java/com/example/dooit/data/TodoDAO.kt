package com.example.dooit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDAO {
    @Query("SELECT * FROM TODO_LIST ")
    fun getAllListItems(): List<TodoItemWithTask>
    @Query("SELECT * FROM TODO_LIST WHERE id =:id")
    fun getListItem(id: Int): TodoItemWithTask

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem( todoList: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoList( todoItem: TodoListEntity): Long

    @Update
  suspend  fun updateTodoList( todoLis: TodoListEntity)

    @Update
   suspend fun updateTodoItem( todoItem: TodoItemEntity)

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insertImage(image: ImageEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAudio(audiEntity: AudiEntity): Long



}