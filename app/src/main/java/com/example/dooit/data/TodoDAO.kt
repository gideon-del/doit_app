package com.example.dooit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TodoDAO {
    @Query("SELECT * FROM TODO_LIST ")
    fun getAllListItems(): List<TodoItemWithTask>
    @Query("SELECT * FROM TODO_LIST WHERE id =:id")
    fun getListItem(id: Int): List<TodoItemWithTask>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItem()

}