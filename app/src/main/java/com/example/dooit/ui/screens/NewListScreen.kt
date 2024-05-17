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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dooit.R
import com.example.dooit.ui.theme.DooitTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

data class ListItemState(
    val id: Int,
    var item: String,
    var currentPosition: Offset,
    var offsetY: Float
)

class NewListViewModel : ViewModel() {
    private var _uiState = MutableStateFlow(
        mutableListOf(
            ListItemState(item = "Item 1", id = 1, currentPosition = Offset.Zero, offsetY = 0f),
            ListItemState(item = "Item 2", id = 2, currentPosition = Offset.Zero, offsetY = 0f)
        )
    )
    val uiState: StateFlow<MutableList<ListItemState>>
        get() = _uiState

    fun changeOffset(id: Int, offsetY: Float) {
        val newList = _uiState.value.map {
            if (it.id == id) {
                it.copy(
                    offsetY = offsetY + it.offsetY,
//                    currentPosition = it.currentPosition
                )

            } else {
                it
            }
        }
//        _uiState.update { itemList ->
//            val item = itemList.find {
//                it.id == id
//            }
//            if(item!=null){
//                item.offsetY += offsetY
//
//            }
//            Log.d("Current", itemList.toString())
//            itemList
//
//        }
//        _
//        Log.d("Current", uiState.value.toString())
        _uiState.value = newList.toMutableList()
        Log.d("Current", _uiState.value.toString())
    }

    fun setGlobalPosition(id: Int, position: Offset) {
        val newPositionList = _uiState.value.map {
            if (it.id == id) {
                it.copy(
                    currentPosition = position
                )
            } else {
                it
            }
        }


        _uiState.value = newPositionList.toMutableList()
        Log.d("Current", _uiState.value.toString())
    }

    fun changePosition(id: Int, position: Offset) {
        _uiState.value = _uiState.value.map { item ->
            if (item.id == id) {
                item.currentPosition = position
                item.offsetY = position.y
                item
            } else {
                item
            }
        }.toMutableList()
        Log.d("Current", _uiState.value.toString())


    }

    fun calculateNextIndex(id: Int): Float {
        val item = _uiState.value.find { it.id == id }
        if (item == null) {
            return 0f
        } else {
            val mainOffset = item.offsetY / 100
            return mainOffset
        }
    }
}

@Composable
fun NewListScreen(modifier: Modifier = Modifier, screenViewModel: NewListViewModel = viewModel()) {
    val mainState by screenViewModel.uiState.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { NewListTopBar(modifier = Modifier.padding(horizontal = 10.dp)) }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(modifier= Modifier.weight(1f).fillMaxWidth()) {
                Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                    TextField(
                        value = "Title",
                        onValueChange = {/*TODO*/ },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = {/*TODO*/ },
                            modifier = Modifier.clip(shape = RoundedCornerShape(100.dp))
                        )
                        TextField(
                            value = "",
                            onValueChange = {},
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.padding(0.dp),
                            placeholder = { Text(text = "To-do", color = Color(0xFF8C8E8F)) })
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
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.padding(0.dp),
                            placeholder = { Text(text = "To-do", color = Color(0xFF8C8E8F)) })
                    }

                }
            }

            Column(modifier= Modifier.padding(horizontal = 8.dp, vertical = 20.dp)) {
                Text(text = "Choose a label", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 3.dp, alignment = Alignment.CenterHorizontally),
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Button(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Personal")
                    }

                    Button(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF898989),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Work")
                    }

                    Button(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF898989),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Finance")
                    }

                    Button(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF898989),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Other", softWrap = false)
                    }

                }

            }
//            LazyColumn {
//                items(mainState) {item->
//                    ListItem(
//                        itemIndex = mainState.indexOf(item),
//                        totalSize = mainState.size,
//                        item = item,
//                        modifier = Modifier.onGloballyPositioned {coordinates ->
//                            println(coordinates.positionInWindow().toString())
//                            screenViewModel.setGlobalPosition(item.id ,coordinates.positionInWindow())
//                        },
//                        dragEvent = {offsetY ->
//                            screenViewModel.changeOffset(item.id, offsetY)
//                        },
//                        getItemList ={
//                            Log.d("Current", it.toString())
//                            if(it <= mainState.size){
//                                mainState[it]
//                            } else {
//                                null
//                            }
//
//
//                        },
//                        dropItem = { item, currentPosition ->
//                            screenViewModel.changePosition(item.id,currentPosition)
//                        },
//                        calculatePosition = {
//
//                            screenViewModel.calculateNextIndex(it)
//                        }
//                        )
//
//
//                }
//            }


        }
    }
}

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    itemIndex: Int,
    totalSize: Int,
    dragEvent: (Float) -> Unit,
    item: ListItemState,
    getItemList: (id: Int) -> ListItemState?,
    dropItem: (item: ListItemState, currentPosition: Offset) -> Unit,
    calculatePosition: (Int) -> Float

) {

    Button(
        onClick = {},
        modifier = modifier
            .width(100.dp)
            .height(100.dp)
            .offset {
                IntOffset(x = 0, y = item.offsetY.roundToInt())
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        Log.d(
                            "Current",
                            "${item.offsetY} ,${item.offsetY},${item.toString()},"
                        )
                        val mainOffset = calculatePosition(item.id)
                        val nextItem = mainOffset.toInt()
//                            .coerceIn(
//                                if (itemIndex == 0) 0 else -itemIndex,
//                                if (totalSize - itemIndex == 1) 0 else totalSize - itemIndex
//                            )
                        Log.d(
                            "Current",
                            "${
                                IntOffset(
                                    x = 0,
                                    y = nextItem
                                )
                            }, $mainOffset, $itemIndex ,${nextItem - itemIndex}"
                        )
                        val dropTarget = getItemList(nextItem + itemIndex)
                        Log.d("Current", dropTarget.toString())
                        if (dropTarget == item) {

                            dropItem(item, item.currentPosition)
                        } else {
                            val tempCordinate = getItemList(itemIndex)!!.currentPosition
                            dropItem(item, dropTarget!!.currentPosition)
                            dropItem(dropTarget, tempCordinate)

                        }

                    },
                ) { change, dragAmount ->

                    change.consume()

                    dragEvent(dragAmount.y)


//                    Log.d("Current", dragAmount.y.toString())
                }


            }
    ) {

        Text(text = "${item.currentPosition.y}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewListTopBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(title = { /*TODO*/ }, modifier = modifier, navigationIcon = {
        IconButton(onClick = { /*TODO*/ }) {
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
            NewListScreen()
        }

    }
}