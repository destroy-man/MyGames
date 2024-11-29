package ru.korobeynikov.mygames.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import ru.korobeynikov.mygames.di.gameViewModelModule

class MainActivity : ComponentActivity() {

    companion object {
        const val REQUEST_CODE_SAVE_GAMES = 1
        const val REQUEST_CODE_LOAD_GAMES = 2
        const val REQUEST_CODE_LOAD_GENRES = 3

    }

    private val path = Environment.getExternalStorageDirectory().absolutePath
    private var numOperation = 0
    private val gameViewModel: GameViewModel by inject()

    private fun processPermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", this.packageName))
            launcherManageStorage.launch(intent)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            launcherManageStorage.launch(intent)
        }
    }

    private val launcherManageStorage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (numOperation) {
                REQUEST_CODE_SAVE_GAMES -> gameViewModel.saveGames(path, showMessage)
                REQUEST_CODE_LOAD_GAMES -> gameViewModel.loadGames(path, showMessage)
                REQUEST_CODE_LOAD_GENRES -> gameViewModel.loadGenres(path)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stopKoin()
        startKoin {
            androidContext(this@MainActivity)
            modules(gameViewModelModule)
        }
        gameViewModel.getGames()
        numOperation = REQUEST_CODE_LOAD_GENRES
        if (Environment.isExternalStorageManager())
            gameViewModel.loadGenres(path)
        else
            processPermission()
        setContent {
            val navHostController = rememberNavController()
            Column {
                NavHost(navController = navHostController, startDestination = "game") {
                    composable("game") {
                        GameScreen(
                            gameViewModel,
                            onShowMessage = showMessage,
                            onNavigateToGenre = {
                                navHostController.navigate("genre")
                            },
                            onSaveGames = {
                                numOperation = REQUEST_CODE_SAVE_GAMES
                                if (Environment.isExternalStorageManager())
                                    gameViewModel.saveGames(path, showMessage)
                                else
                                    processPermission()
                            },
                            onLoadGames = {
                                numOperation = REQUEST_CODE_LOAD_GAMES
                                if (Environment.isExternalStorageManager())
                                    gameViewModel.loadGames(path, showMessage)
                                else
                                    processPermission()
                            })
                    }
                    composable("genre") {
                        GenreScreen(gameViewModel) { navHostController.navigate("game") }
                    }
                }
            }
        }
    }

    private val showMessage: (String) -> Unit = { textMessage ->
        Toast.makeText(this@MainActivity, textMessage, Toast.LENGTH_SHORT).show()
    }
}