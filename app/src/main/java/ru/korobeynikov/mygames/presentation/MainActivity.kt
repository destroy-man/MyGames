package ru.korobeynikov.mygames.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameViewModel = GameViewModel()
        setContent {
            val navHostController = rememberNavController()
            Column {
                NavHost(navController = navHostController, startDestination = "game") {
                    composable("game") {
                        GameScreen(gameViewModel) { navHostController.navigate("genre") }
                    }
                    composable("genre") {
                        GenreScreen(gameViewModel) { navHostController.navigate("game") }
                    }
                }
            }
        }
    }
}