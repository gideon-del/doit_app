package com.example.dooit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 5,
    entities = [TodoItemEntity::class, TodoListEntity::class],
    exportSchema = false
)
abstract class TodoListDB() : RoomDatabase() {
    abstract fun todoListDAO(): TodoDAO

    companion object {
        @Volatile
        var Instance: TodoListDB? = null

        fun getDatabase(context: Context): TodoListDB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TodoListDB::class.java, "todo_db")
                    .fallbackToDestructiveMigration().build()
            }.also {
                Instance = it
            }
        }
    }
}