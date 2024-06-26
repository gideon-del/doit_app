package com.example.dooit.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dooit.R
import com.example.dooit.ui.doitviewmodels.HomeScreenViewModel
import com.example.dooit.ui.theme.DooitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.Factory),
    navigateToList: (id: Int) -> Unit,
    navigateToNewList: () -> Unit,
) {
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){
            homeViewModel.getAllList()
        }
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .widthIn(min = 121.dp)
                        .padding(start = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = stringResource(
                            id = R.string.app_name
                        ),
                        modifier = Modifier.fillMaxSize(0.9f),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
                }
            },
            modifier = Modifier.padding(bottom = 20.dp),

            )
    },
        floatingActionButton = {
            if (homeUiState.todoLists.isNotEmpty()) {
                IconButton(onClick = {
                    navigateToNewList()
                }, modifier= Modifier
                    .width(65.dp)
                    .height(65.dp)) {
                    Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "Add List")
                }
            }
        }) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(50.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(47.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(
                        color = Color(0xFFE5E5E5)
                    ),

                ) {
                FilledTonalButton(
                    onClick = {
                        homeViewModel.toggleShowAll(showAll = true)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (homeUiState.showAll) Color.Black else Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(1 / 2f)

                ) {
                    Text(text = "All List")
                }
                Button(
                    onClick = { homeViewModel.toggleShowAll(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!homeUiState.showAll) Color.Black else Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight()
                ) {
                    Text(text = "Pinned")
                }
            }
            if (homeUiState.todoLists.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.todo_svg),
                            contentDescription = null,
                            modifier = Modifier
                                .width(385.dp)
                                .height(202.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Create your first to-do list...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { navigateToNewList() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black, contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.heightIn(min = 50.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = "New List", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                homeUiState.todoLists.forEach {
                    Card(
                        onClick = {
                                  navigateToList(it.todoList.id)
                        },
                        modifier = Modifier.clip(
                            RoundedCornerShape(9.dp)
                        ),
                        border = BorderStroke(2.dp,Color.Black)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier

                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = it.todoList.title,
                                style = MaterialTheme.typography.displayLarge
                            )
                            Row {
                                if (it.todoList.label != null) {
                                    Text(
                                        text = it.todoList.label,
                                        modifier = Modifier
                                            .background(color = Color.Black)
                                            .padding(horizontal = 4.dp, vertical = 8.dp)
                                            .clip(
                                                RoundedCornerShape(10.dp)
                                            ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }

                            }
                        }
                    }

                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DooitTheme {
        HomeScreen(navigateToList = {}, navigateToNewList = {})
    }
}