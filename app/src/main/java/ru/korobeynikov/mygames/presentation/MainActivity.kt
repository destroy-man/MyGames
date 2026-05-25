package ru.korobeynikov.mygames.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import ru.korobeynikov.mygames.di.gameViewModelModule
import ru.korobeynikov.mygames.presentation.game.GameScreen
import ru.korobeynikov.mygames.presentation.game.GameViewModel

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stopKoin()
        startKoin {
            androidContext(this@MainActivity)
            modules(gameViewModelModule)
        }
        gameViewModel.getGames()
        setContent {
            val navHostController = rememberNavController()
            Column(modifier = Modifier.safeContentPadding()) {
                NavHost(navController = navHostController, startDestination = "game") {
                    composable("game") {
                        GameScreen(
                            gameViewModel,
                            onNavigateToGenre = {
                                navHostController.navigate("genre")
                            }
                        )
                    }
                    composable("genre") {
                        GenreScreen(gameViewModel) { navHostController.navigate("game") }
                    }
                }
            }
        }
    }
}