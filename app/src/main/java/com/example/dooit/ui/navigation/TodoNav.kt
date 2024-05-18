package com.example.dooit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.dooit.ui.screens.HomeScreen
import com.example.dooit.ui.screens.NewListScreen
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
@Composable
fun TodoNav(modifier: Modifier=Modifier) {
 val navController: NavController = rememberNavController()
  NavHost(navController = navController as NavHostController, startDestination = HomeScreenRoute){
   composable<HomeScreenRoute> {
     HomeScreen(navigateToList = {

      navController.navigate(NewTodoRoute)
     })
   }
   composable<NewTodoRoute> {
  
    NewListScreen(id = null)
   }
   composable<TodoRoute> {
    val args = it.toRoute<TodoRoute>()
    NewListScreen(id = args.id)
   }
  }
}