package com.example.dooit.ui.doitviewmodels


import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dooit.data.AudiEntity
import com.example.dooit.data.ImageEntity
import com.example.dooit.data.TodoItemEntity
import com.example.dooit.data.TodoItemWithTask
import com.example.dooit.data.TodoListEntity
import com.example.dooit.data.TodoRepo
import com.example.dooit.data.todoApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOError
import java.io.IOException


sealed class TodoListUIStates {
    data class Success(val item: TodoItemWithTask) : TodoListUIStates()
    object Loading : TodoListUIStates()
    object Error : TodoListUIStates()
}

class NewListViewModel(private val todoRepo: TodoRepo) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoListUIStates>(TodoListUIStates.Loading)
    val uiState = _uiState.asStateFlow()
    private var recorder: MediaRecorder? = null
    private var fileName =""
    var player: MediaPlayer? = null
    fun getList(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            _uiState.value = try {
                val todoList = todoRepo.getListItem(id)
                TodoListUIStates.Success(todoList)
            } catch (e: IOError) {
                TodoListUIStates.Error
            }
        }

    }

    fun startRecording(context: Context, outputFile: File) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            recorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(FileOutputStream(outputFile).fd)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    prepare()
                }catch (e: IOException){
                    Log.e("Audio","prepare failed")
                }
                start()
            }
        } else {
            recorder =  MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    prepare()
                }catch (e: IOException){
                    Log.e("Audio","prepare failed")
                }
                start()
            }
        }

    }
    fun stopRecording(outputFile: File, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                recorder?.apply {
                    stop()
                    release()
                }



                recorder = null

                todoRepo.insertAudio(AudiEntity(uri = outputFile.toUri().toString(), todoListId = (_uiState.value as TodoListUIStates.Success).item.todoList.id ))
                val updatedItem = todoRepo.getListItem((_uiState.value as TodoListUIStates.Success).item.todoList.id)
                _uiState.value = TodoListUIStates.Success(item = updatedItem)
            }
        }

    }
    fun startPlayer(uri: Uri, context: Context) {
        player = MediaPlayer().apply {
            try {
                setDataSource(context, uri)
                prepare()
                start()
            }catch (e: IOException){
                e.printStackTrace()
                Log.e("Player", "prepare() failed")
            }

        }
    }
    fun pausePlayer() {
        player?.pause()
    }
    fun resumePlayer() {
        player?.start()
    }
    fun stopPlayer() {
        player?.apply {
            stop()
            release()
        }

    }
    suspend fun createNewList() {

        _uiState.value = try {
            val newList = todoRepo.insertTodoList(TodoListEntity(title = "Title", label = ""))

            val newItem = TodoItemEntity(todoItemId = newList.toInt(), task = "To do")
            todoRepo.insertItem(newItem)
            Log.d("Current", "New Todo Inserted ${newItem} ${newList}")
            val mainList = todoRepo.getListItem(newList.toInt())

            TodoListUIStates.Success(mainList)
        } catch (e: IOError) {
            TodoListUIStates.Error
        }


    }

    fun updateTodoItem(todoItemEntity: TodoItemEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.value = try {
//                   todoRepo.updateTodoItem(todoItemEntity)
//                   val  updatedList = todoRepo.getListItem((_uiState.value as TodoListUIStates.Success).item.todoList.id)
                    val currentState = (_uiState.value as TodoListUIStates.Success).item
                    TodoListUIStates.Success(currentState.copy(
                        items = currentState.items.map {
                            if (it.id == todoItemEntity.id) {
                                todoItemEntity
                            } else {
                                it
                            }
                        }
                    ))
                } catch (e: IOError) {
                    TodoListUIStates.Error
                }
                todoRepo.updateTodoItem(todoItemEntity)
            }

        }


    }

    fun updateTodoList(listEntity: TodoListEntity) {
        viewModelScope.launch {
            _uiState.value = try {
                val currentList = (_uiState.value as TodoListUIStates.Success).item
                TodoListUIStates.Success(
                    currentList.copy(
                        todoList = listEntity
                    )
                )
            } catch (e: IOError) {
                TodoListUIStates.Error
            }
            todoRepo.updateTodoList(listEntity)
        }
    }

    fun uploadImage(imageUri: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                todoRepo.insertImage(
                    ImageEntity(
                        uri = imageUri,
                        todoListId = (_uiState.value as TodoListUIStates.Success).item.todoList.id
                    )
                )
                _uiState.value = try {
                    val newState = todoRepo.getListItem((_uiState.value as TodoListUIStates.Success).item.todoList.id)
                    TodoListUIStates.Success(item = newState)
                } catch (e:IOError) {
                    TodoListUIStates.Error
                }
            }

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