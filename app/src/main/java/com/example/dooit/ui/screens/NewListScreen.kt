package com.example.dooit.ui.screens


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.sharp.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.dooit.R
import com.example.dooit.data.TodoItemEntity
import com.example.dooit.data.TodoItemWithTask
import com.example.dooit.data.TodoListEntity
import com.example.dooit.ui.LoadingScreen
import com.example.dooit.ui.doitviewmodels.NewListViewModel
import com.example.dooit.ui.doitviewmodels.TodoListUIStates
import com.example.dooit.ui.theme.DooitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File


@Composable
fun NewListScreen(
    modifier: Modifier = Modifier,
    screenViewModel: NewListViewModel = viewModel(factory = NewListViewModel.Factory),
    id: Int?,
    navigateToHome: () -> Unit
) {
    val listUIStates by screenViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val getContent =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { imageUri ->
            if (imageUri != null && listUIStates is TodoListUIStates.Success) {
                val tags =
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(imageUri, tags)
                screenViewModel.uploadImage(imageUri.toString())


            }
        }

    if (id == null) {
        LaunchedEffect(Unit) {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    Log.d("Current", "New Todo")
                    screenViewModel.createNewList()
                }

            }
        }
    } else {
        screenViewModel.getList(id)
    }

    when (listUIStates) {
        is TodoListUIStates.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is TodoListUIStates.Error -> ErrorScreen(modifier = modifier.fillMaxSize(), onRetry = {})
        is TodoListUIStates.Success -> {
            SuccessScreen(
                listItem = (listUIStates as TodoListUIStates.Success).item,
                onChangeTodo = {
                    screenViewModel.updateTodoItem(it)
                },
                onUpdateTodoList = {
                    screenViewModel.updateTodoList(it)
                },
                navigateToHome = navigateToHome,
                uploadImage = {
                    getContent.launch(arrayOf("image/*"))
                },
                screenViewModel = screenViewModel
            )
        }
    }
}

@Composable
fun ImageList(modifier: Modifier = Modifier, uri: String) {
    val permission =
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )

    }
    val requestLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            hasPermission = it
        }
    LaunchedEffect(key1 = Unit) {
        if (!hasPermission) {
            requestLauncher.launch(permission)
        }
    }
    if (hasPermission) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .build(),
            contentDescription = null,
            modifier = modifier
                .heightIn(min = 200.dp, max = 300.dp)
                .fillMaxWidth(),
            placeholder = painterResource(id = R.drawable.todo_svg),
            error = painterResource(id = R.drawable.ic_launcher_background),
            onLoading = {
            },
            onError = {
                it.result.throwable.printStackTrace()
                Log.d(
                    "Image",
                    "${it.painter.toString()} ${it.result.throwable.stackTrace}"
                )
            }
        )
    }
}

sealed class PlayerStatus {
    object Playing : PlayerStatus()
    object Paused : PlayerStatus()
    object Idle : PlayerStatus()
}

@Composable
fun AudioList(
    modifier: Modifier = Modifier,
    onStartPlayer: () -> Unit,
    onPausePlayer: () -> Unit,
    onStopPlayer: () -> Unit,
    onResumePlayer: () -> Unit
) {
    var playerStatus: PlayerStatus by remember {
        mutableStateOf(PlayerStatus.Idle)
    }
    Row(modifier = modifier) {
        IconButton(onClick = {
            when (playerStatus) {
                is PlayerStatus.Idle -> {
                    onStartPlayer()
                    playerStatus = PlayerStatus.Playing
                }

                is PlayerStatus.Playing -> {
                    onPausePlayer()
                    playerStatus = PlayerStatus.Paused
                }

                is PlayerStatus.Paused -> {
                    onResumePlayer()
                    playerStatus = PlayerStatus.Playing
                }
            }
        }) {
            if(playerStatus is PlayerStatus.Idle || playerStatus is PlayerStatus.Paused){
                Icon(
                    imageVector =Icons.Filled.PlayArrow, contentDescription = null
                )
            }else{
              Icon(painter = painterResource(id = R.drawable.pause_icon), contentDescription ="Pause Music" )  
            }
            
        }
        if (playerStatus is PlayerStatus.Playing || playerStatus is PlayerStatus.Paused) {
            IconButton(onClick = {
                onStopPlayer()
                playerStatus = PlayerStatus.Idle
            }) {
                Icon(painter = painterResource(id = R.drawable.stop_icon), contentDescription = "Stop Player")
            }
        }
    }
}

@Composable
fun SuccessScreen(
    modifier: Modifier = Modifier,
    listItem: TodoItemWithTask,
    onChangeTodo: (TodoItemEntity) -> Unit,
    onUpdateTodoList: (TodoListEntity) -> Unit,
    uploadImage: () -> Unit,
    navigateToHome: () -> Unit,
    screenViewModel: NewListViewModel
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NewListTopBar(
                modifier = Modifier.padding(horizontal = 10.dp),
                navigateToHome
            )
        }) {
        Column(modifier = Modifier.padding(it)) {

            Column {

                listItem.images.forEach { image ->

                    ImageList(uri = image.uri)


                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val context = LocalContext.current
                    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                        TextField(
                            value = listItem.todoList.title,
                            onValueChange = {
                                onUpdateTodoList(
                                    listItem.todoList.copy(
                                        title = it
                                    )
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            )
                        )

                    }
                    Column {
                        listItem.items.forEach { todoItem ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = todoItem.isDone,
                                    onCheckedChange = {
                                        onChangeTodo(
                                            todoItem.copy(
                                                isDone = it
                                            )
                                        )
                                    },
                                    modifier = Modifier.clip(shape = RoundedCornerShape(100.dp))
                                )
                                TextField(
                                    value = todoItem.task,
                                    onValueChange = {
                                        onChangeTodo(
                                            todoItem.copy(
                                                task = it
                                            )
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.padding(0.dp),
                                    placeholder = {
                                        Text(
                                            text = "To-do",
                                            color = Color(0xFF8C8E8F)
                                        )
                                    })
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_btn),
                                    contentDescription = null,
                                    modifier = Modifier.sizeIn(
                                        minWidth = 25.dp,
                                        minHeight = 25.dp
                                    )
                                )
                            }
                            TextField(
                                value = "",
                                onValueChange = {},
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.padding(0.dp),
                                placeholder = {
                                    Text(
                                        text = "To-do",
                                        color = Color(0xFF8C8E8F)
                                    )
                                })
                        }
                        if (listItem.images.isEmpty()) {
                            Button(onClick = uploadImage) {
                                Text(text = stringResource(R.string.add_image))
                            }
                        }

                        if (listItem.audio.isEmpty()) {
                            var audioFile: File? by remember {
                                mutableStateOf(null)
                            }

                            val dir = context.cacheDir
                            val startRecording = {
                                audioFile = File(
                                    dir,
                                    "${listItem.todoList.id}${listItem.audio.size + 1}.mp3"
                                )
                                screenViewModel.startRecording(context, audioFile!!)
                            }
                            val stopRecording = {
                                screenViewModel.stopRecording(audioFile!!, context)
                                audioFile = null
                            }
                            RecordButton(
                                onStartRecord = startRecording,
                                onStopRecord = stopRecording
                            )
                        }
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(items = listItem.audio) { audioFile ->
                                AudioList(
                                    onStartPlayer = {
                                        screenViewModel.startPlayer(
                                            Uri.parse(audioFile.uri),
                                            context
                                        )
                                    },
                                    onPausePlayer = {
                                        screenViewModel.pausePlayer()
                                    },
                                    onStopPlayer = {
                                        screenViewModel.stopPlayer()
                                    },
                                    onResumePlayer = {
                                        screenViewModel.resumePlayer()
                                    })
                            }
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 20.dp)) {
                val labels = listOf("Personal", "Work", "Finance", "Other")
                Text(text = "Choose a label", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(20.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 3.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    items(items = labels) {
                        Button(
                            onClick = {
                                onUpdateTodoList(
                                    listItem.todoList.copy(
                                        label = it
                                    )
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (listItem.todoList.label == it) Color.Black else Color(
                                    0xFF898989
                                ),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = it)
                        }
                    }


                }

            }
        }


    }
}

@Composable
fun RecordButton(
    modifier: Modifier = Modifier,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit
) {
    var recording by remember {
        mutableStateOf(false)
    }

    Button(onClick = {
        if (recording) {
            onStopRecord()
            recording = false
        } else {
            onStartRecord()
            recording = true
        }
    }) {
        Text(text = if (recording) "Stop Recording" else "Add audio")
    }
}


@Composable
fun ErrorScreen(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(modifier = modifier) {
        Text(text = "Error")
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewListTopBar(modifier: Modifier = Modifier, navigateToHome: () -> Unit) {
    CenterAlignedTopAppBar(title = { /*TODO*/ }, modifier = modifier, navigationIcon = {
        IconButton(onClick = navigateToHome) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Arrow Back")
        }

    },
        actions = {
            OutlinedButton(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pin),
                    contentDescription = null,
                    modifier = Modifier.height(10.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Pin")
            }
            Column {
                val showDropDown by remember {
                    mutableStateOf(false)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { /*TODO*/ }) {
                    Button(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.image_icon),
                            contentDescription = "Add Image"
                        )
                        Text(text = "Add Image")
                    }
                    Button(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.mic_icon),
                            contentDescription = "Add Image"
                        )
                        Text(text = "Add Image")
                    }
                }
            }
        })
}

@Preview
@Composable
fun NewListScreenPreview() {
    DooitTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NewListScreen(id = null, navigateToHome = {})
        }

    }
}