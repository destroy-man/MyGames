package ru.korobeynikov.mygames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            Column {
                NavHost(navController = navHostController, startDestination = "game/") {
                    composable("game/{dataGame}", arguments = listOf(navArgument("dataGame") {
                        type = NavType.StringType
                    })) { stackEntry ->
                        val dataGame = stackEntry.arguments?.getString("dataGame")
                        dataGame?.let { data ->
                            GameScreen(data) { dataGameToGenreScreen ->
                                navHostController.navigate("genre/$dataGameToGenreScreen")
                            }
                        } ?: GameScreen("") {}
                    }
                    composable("genre/{dataGame}", arguments = listOf(navArgument("dataGame") {
                        type = NavType.StringType
                    })) { stackEntry ->
                        val dataGame = stackEntry.arguments?.getString("dataGame")
                        if (dataGame != null)
                            GenreScreen(dataGame) { dataGameToGameScreen ->
                                navHostController.navigate("game/$dataGameToGameScreen")
                            }
                    }
                }
            }
        }
    }
}