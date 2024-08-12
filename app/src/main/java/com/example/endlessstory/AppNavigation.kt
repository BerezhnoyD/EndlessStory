package com.example.endlessstory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class AppNavigation() {
    Start,
    Choice,
    Game,
    Summary
}

@Composable
fun EndlessApp(
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = AppNavigation.valueOf(
        backStackEntry?.destination?.route ?: AppNavigation.Start.name
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.inversePrimary,
    ) {
        NavHost(navController = navController,
                startDestination = AppNavigation.Start.name)
        {
            composable(route = AppNavigation.Start.name) {
                InstructionsScreen(
                    onNextButtonClicked = {
                        navController.navigate(AppNavigation.Choice.name)
                    })
            }
            composable(route = AppNavigation.Choice.name) {
                val context = LocalContext.current
                CustomizeScreen(
                    onNextButtonClicked = {
                        navController.navigate(AppNavigation.Game.name)
                    })
            }
            composable(route = AppNavigation.Game.name) {
                val context = LocalContext.current
                StoryScreen(
                    onNextButtonClicked = {
                        navController.navigate(AppNavigation.Summary.name)
                    })
            }
            composable(route = AppNavigation.Summary.name) {
                val context = LocalContext.current
                InstructionsScreen(
                    onNextButtonClicked = {
                        navController.navigate(AppNavigation.Summary.name)
                    })
            }
        }
    }

    }