package com.example.dooit.data


import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Entity(tableName = "todo_list")
data class TodoListEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title: String,
    @ColumnInfo(name="is_pinned")
    val isPinned: Boolean= false,
    val label: String? = ""
)

@Entity(tableName = "todo_item")
data class TodoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val isDone:Boolean = false,
    val task: String,
    val todoItemId: Int
)

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String,
    val todoListId: Int
)
@Entity(tableName = "audio")
data class AudiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String,
    val todoListId: Int
)
data class TodoItemWithTask(
    @Embedded val todoList: TodoListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "todoItemId"
    )
    val items: List<TodoItemEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "todoListId"
    )
    val images: List<ImageEntity>,
    @Relation(
parentColumn = "id",
entityColumn = "todoListId"
)
val audio: List<AudiEntity>
    )
