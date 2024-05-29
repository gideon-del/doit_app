package com.example.dooit.ui.screens


import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dooit.R
import com.example.dooit.data.TodoItemEntity
import com.example.dooit.data.TodoItemWithTask
import com.example.dooit.data.TodoListEntity
import com.example.dooit.ui.doitviewmodels.NewListViewModel
import com.example.dooit.ui.doitviewmodels.TodoListUIStates
import com.example.dooit.ui.theme.DooitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext


@Composable
fun NewListScreen(
    modifier: Modifier = Modifier,
    screenViewModel: NewListViewModel = viewModel(factory = NewListViewModel.Factory),
    id: Int?,
    navigateToHome: () -> Unit
) {
    val listUIStates by screenViewModel.uiState.collectAsState()
    val getContent =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { imageUri ->
            if (imageUri != null && listUIStates is TodoListUIStates.Success) {
                screenViewModel.uploadImage(imageUri.toString())
            }
        }

    if (id == null) {
        LaunchedEffect(key1 = "Main") {
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
                    getContent.launch("image/*")
                }
            )
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
    navigateToHome: () -> Unit
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NewListTopBar(
                modifier = Modifier.padding(horizontal = 10.dp),
                navigateToHome
            )
        }) {

        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            item {
                listItem.images.forEach { image ->

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Uri.parse(image.uri))
                            .build(), contentDescription = null,
                        modifier = Modifier
                            .heightIn(min = 200.dp, max = 300.dp)
                            .fillMaxWidth()
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                    modifier = Modifier.sizeIn(minWidth = 25.dp, minHeight = 25.dp)
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
                                placeholder = { Text(text = "To-do", color = Color(0xFF8C8E8F)) })
                        }
                        if (listItem.images.isEmpty()) {
                            Button(onClick = uploadImage) {
                                Text(text = stringResource(R.string.add_image))
                            }
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
               items(items = labels){
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

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background)) {
        CircularProgressIndicator()
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