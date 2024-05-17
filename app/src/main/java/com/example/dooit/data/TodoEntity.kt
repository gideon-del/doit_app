package com.example.dooit.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "todo_list")
data class TodoListEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 1,
    val title: String,
    @ColumnInfo(name="is_pinned")
    val isPinned: Boolean= false
)

@Entity(tableName = "todo_item")
data class TodoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val isDone:Boolean = false,
    val task: String,
    val todoItemId: Int
)


data class TodoItemWithTask(
    @Embedded val todoList: TodoListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "todoItemId"
    )
    val items: List<TodoItemEntity>
    )
