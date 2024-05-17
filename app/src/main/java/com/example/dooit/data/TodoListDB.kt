package com.example.dooit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(version = 1,entities = [TodoItemEntity::class,TodoListEntity::class])
abstract class TodoListDB(): RoomDatabase() {
   abstract fun todoListDAO(): TodoDAO

   companion object{
       var Instance: TodoListDB? = null

       fun getDatabase( context: Context): TodoListDB{
           return Instance ?: synchronized(this){
               Room.databaseBuilder(context, TodoListDB::class.java, "todo_db").build()
           }.also {
               Instance = it
           }
       }
   }
}