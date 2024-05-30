package com.example.dooit.ui.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.dooit.ui.LoadingScreen
import com.example.dooit.ui.doitviewmodels.TodoNavState
import com.example.dooit.ui.doitviewmodels.TodoNavViewModel
import com.example.dooit.ui.screens.HomeScreen
import com.example.dooit.ui.screens.NewListScreen
import com.example.dooit.ui.screens.WelcomeScreen
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
object HomeScreenRoute

@Serializable
data class TodoRoute(
    val id: Int
)

@Serializable
object NewTodoRoute

@Serializable

object WelcomeScreenRoute
@Composable
fun TodoNav(modifier: Modifier = Modifier,  todoNavViewModel: TodoNavViewModel = viewModel(
    factory = TodoNavViewModel.Factory
)) {
    val navController: NavController = rememberNavController()
    val todoState by todoNavViewModel.uiState.collectAsStateWithLifecycle()


  when(todoState) {
      is TodoNavState.Success ->  NavHost(
          navController = navController as NavHostController,
          startDestination = if((todoState as TodoNavState.Success).isFirstTime) HomeScreenRoute else WelcomeScreenRoute,
          modifier = modifier
      ) {
          composable<WelcomeScreenRoute>{

              WelcomeScreen(changeStatus = {
                  todoNavViewModel.changeStatus()
                  navController.popBackStack(route = HomeScreenRoute, inclusive = true)
              })
          }
          composable<HomeScreenRoute> {
              HomeScreen(navigateToList = {

                  navController.navigate(TodoRoute(it))
              }, navigateToNewList = {
                  navController.navigate(NewTodoRoute)
              })
          }
          composable<NewTodoRoute> {

              NewListScreen(id = null, navigateToHome = {
                  navController.popBackStack()
              })
          }
          composable<TodoRoute> {
              val args = it.toRoute<TodoRoute>()
              NewListScreen(id = args.id,  navigateToHome = {
                  navController.popBackStack()
              })
          }
      }
      is TodoNavState.Loading -> LoadingScreen()
  }
}