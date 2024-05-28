package com.example.dooit.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dooit.R
import com.example.dooit.data.TodoItemEntity
import com.example.dooit.data.TodoItemWithTask
import com.example.dooit.data.TodoListEntity
import com.example.dooit.ui.doitviewmodels.ListItemState
import com.example.dooit.ui.doitviewmodels.NewListViewModel
import com.example.dooit.ui.doitviewmodels.TodoListUIStates
import com.example.dooit.ui.theme.DooitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Composable
fun NewListScreen(
    modifier: Modifier = Modifier,
    screenViewModel: NewListViewModel = viewModel(factory = NewListViewModel.Factory),
    id: Int?,
    navigateToHome:() -> Unit
) {
    val listUIStates by screenViewModel.uiState.collectAsState()

    val mainScope = rememberCoroutineScope()
    if (id == null) {
        LaunchedEffect(key1 = "Main") {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    Log.d("Current", "New Todo")
                    screenViewModel.createNewList()
                }

            }
        }
    }else{
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
                navigateToHome = navigateToHome)
        }
    }
}

@Composable
fun SuccessScreen(
    modifier: Modifier = Modifier,
    listItem: TodoItemWithTask,
    onChangeTodo: (TodoItemEntity) -> Unit,
    onUpdateTodoList:(TodoListEntity) -> Unit,

navigateToHome: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { NewListTopBar(modifier = Modifier.padding(horizontal = 10.dp), navigateToHome) }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                    TextField(
                        value = listItem.todoList.title,
                        onValueChange = {
                            onUpdateTodoList(listItem.todoList.copy(
                                title = it
                            ))
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
                                placeholder = { Text(text = "To-do", color = Color(0xFF8C8E8F)) })
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

                }
            }

            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 20.dp)) {
                val labels = listOf("Personal", "Work", "Finance", "Other")
                Text(text = "Choose a label", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 3.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    labels.forEach {
                        Button(
                            onClick = {
                                onUpdateTodoList(listItem.todoList.copy(
                                    label = it
                                ))
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
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(modifier = Modifier) {
        Text(text = "Loading")
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
        IconButton(onClick =navigateToHome) {
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