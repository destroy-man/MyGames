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
import androidx.room.Room
import ru.korobeynikov.mygames.data.GameDatabase
import ru.korobeynikov.mygames.data.GameRepository

class MainActivity : ComponentActivity() {

    companion object {
        const val REQUEST_CODE_PERMISSION_WRITE_STORAGE = 1
        const val REQUEST_CODE_PERMISSION_READ_STORAGE = 2
    }

    private val path = Environment.getExternalStorageDirectory().absolutePath
    private var numOperation = 0
    private lateinit var gameViewModel: GameViewModel

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
            if (numOperation == REQUEST_CODE_PERMISSION_WRITE_STORAGE) {
                gameViewModel.saveGames(path, showMessage)
            } else if (numOperation == REQUEST_CODE_PERMISSION_READ_STORAGE) {
                gameViewModel.loadGames(path, showMessage)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(this, GameDatabase::class.java, "gamesDatabase").build()
        gameViewModel = GameViewModel(GameRepository(db))
        gameViewModel.getGames()
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
                                numOperation = REQUEST_CODE_PERMISSION_WRITE_STORAGE
                                if (Environment.isExternalStorageManager())
                                    gameViewModel.saveGames(path, showMessage)
                                else
                                    processPermission()
                            },
                            onLoadGames = {
                                numOperation = REQUEST_CODE_PERMISSION_READ_STORAGE
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