package ru.korobeynikov.mygames.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import ru.korobeynikov.mygames.data.GameDatabase
import ru.korobeynikov.mygames.data.GameRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(this, GameDatabase::class.java, "gamesDatabase").build()
        val gameViewModel = GameViewModel(GameRepository(db))
        gameViewModel.getGames()
        setContent {
            val navHostController = rememberNavController()
            Column {
                NavHost(navController = navHostController, startDestination = "game") {
                    composable("game") {
                        GameScreen(gameViewModel, onShowMessage = { textMessage ->
                            Toast.makeText(this@MainActivity, textMessage, Toast.LENGTH_SHORT)
                                .show()
                        }, onNavigateToGenre = {
                            navHostController.navigate("genre")
                        })
                    }
                    composable("genre") {
                        GenreScreen(gameViewModel) { navHostController.navigate("game") }
                    }
                }
            }
        }
    }
}