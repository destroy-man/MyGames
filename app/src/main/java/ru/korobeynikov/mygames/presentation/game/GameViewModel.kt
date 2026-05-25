package ru.korobeynikov.mygames.presentation.game

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.korobeynikov.mygames.data.Game
import ru.korobeynikov.mygames.data.GameRepository
import java.io.InputStream
import java.io.OutputStream

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val initialState = GameScreenState(
        nameGame = "",
        ratingGame = "",
        yearGame = "",
        genreGame = "-",
        isSortGames = false,
        listGames = emptyList()
    )
    private val _gameScreenStateFlow = MutableStateFlow(initialState)
    val gameScreenStateFlow: StateFlow<GameScreenState> = _gameScreenStateFlow
    private var gameScreenState = initialState

    fun setGameScreenState(gameScreenState: GameScreenState) {
        this.gameScreenState = gameScreenState
    }

    fun actionChangeName(name: String) {
        _gameScreenStateFlow.value = GameScreenState(
            name,
            gameScreenState.ratingGame,
            gameScreenState.yearGame,
            gameScreenState.genreGame,
            gameScreenState.isSortGames,
            gameScreenState.listGames
        )
    }

    fun actionChangeRating(rating: String) {
        _gameScreenStateFlow.value = GameScreenState(
            gameScreenState.nameGame,
            rating,
            gameScreenState.yearGame,
            gameScreenState.genreGame,
            gameScreenState.isSortGames,
            gameScreenState.listGames
        )
    }

    fun actionChangeYear(year: String) {
        _gameScreenStateFlow.value = GameScreenState(
            gameScreenState.nameGame,
            gameScreenState.ratingGame,
            year,
            gameScreenState.genreGame,
            gameScreenState.isSortGames,
            gameScreenState.listGames
        )
    }

    fun actionChangeGenre(genre: String) {
        _gameScreenStateFlow.value = GameScreenState(
            gameScreenState.nameGame,
            gameScreenState.ratingGame,
            gameScreenState.yearGame,
            genre,
            gameScreenState.isSortGames,
            gameScreenState.listGames
        )
    }

    fun actionChangeSort(isSort: Boolean) {
        _gameScreenStateFlow.value = GameScreenState(
            gameScreenState.nameGame,
            gameScreenState.ratingGame,
            gameScreenState.yearGame,
            gameScreenState.genreGame,
            isSort,
            gameScreenState.listGames
        )
    }

    private fun actionChangeListGames(listGames: List<Game>) {
        _gameScreenStateFlow.value = GameScreenState(
            gameScreenState.nameGame,
            gameScreenState.ratingGame,
            gameScreenState.yearGame,
            gameScreenState.genreGame,
            gameScreenState.isSortGames,
            listGames
        )
    }

    fun getGames() {
        scope.launch {
            val listGames = gameRepository.getGamesFromDB()
            actionChangeListGames(listGames)
        }
    }

    fun addGame(
        nameGame: String,
        ratingGame: String,
        yearGame: String,
        genreGame: String,
        onShowMessage: (String) -> Unit,
    ) {
        scope.launch {
            val message = gameRepository.addGameInDB(
                nameGame,
                ratingGame.toInt(),
                yearGame.toInt(),
                genreGame
            )
            onShowMessage(message)
            getGames()
        }
    }

    fun changeGame(
        nameGame: String,
        ratingGame: String,
        yearGame: String,
        genreGame: String,
        onShowMessage: (String) -> Unit,
    ) {
        scope.launch {
            val message = gameRepository.changeGameInDB(nameGame, ratingGame, yearGame, genreGame)
            onShowMessage(message)
            getGames()
        }
    }

    fun deleteGame(nameGame: String, yearGame: String, onShowMessage: (String) -> Unit) {
        scope.launch {
            val message = gameRepository.deleteGameInDB(nameGame, yearGame.toInt())
            onShowMessage(message)
            getGames()
        }
    }

    fun saveGames(outputStream: OutputStream?, onShowMessage: (String) -> Unit) {
        scope.launch {
            val listGames = gameScreenState.listGames
            if (listGames.isEmpty()) {
                onShowMessage("Нет игр для сохранения")
            } else if (outputStream == null) {
                onShowMessage("Не удалось сохранить игры")
            } else {
                val sbGames = StringBuilder()
                for (game in listGames) {
                    sbGames.appendLine("${game.name};${game.rating};${game.year};${game.genre}")
                }
                outputStream.write(sbGames.toString().toByteArray())
                onShowMessage("Сохранение игр успешно завершено")
            }
        }
    }

    fun loadGames(inputStream: InputStream?, onShowMessage: (String) -> Unit) {
        scope.launch {
            if (inputStream == null) {
                onShowMessage("Не удалось загрузить игры")
            } else {
                val loadedGames = inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
                if (loadedGames.isEmpty()) {
                    onShowMessage("Нет игр для загрузки")
                } else {
                    val games = loadedGames.split("\n")
                    games.forEach { game ->
                        if (game.isEmpty()) return@forEach
                        Log.d("myLogs", game)
                        val nameGame = game.split(";")[0]
                        val ratingGame = game.split(";")[1].toInt()
                        val yearGame = game.split(";")[2].toInt()
                        val genreGame = game.split(";")[3]
                        gameRepository.addGameInDB(nameGame, ratingGame, yearGame, genreGame)
                    }
                    onShowMessage("Игры успешно загружены")
                    getGames()
                }
            }
        }
    }

    fun filterListGames(
        name: String,
        rating: String,
        year: String,
        genre: String,
        isSort: Boolean,
    ): List<Game> {
        var listGames = gameScreenState.listGames
        if (name.isNotEmpty())
            listGames = listGames.filter { it.name.contains(name, true) }
        if (rating.isNotEmpty())
            listGames = listGames.filter { it.rating == rating.toInt() }
        if (year.isNotEmpty())
            listGames = listGames.filter { it.year.toString().contains(year) }
        if (genre != "-")
            listGames = listGames.filter { it.genre == genre }
        if (isSort)
            listGames = listGames.sortedWith(
                compareByDescending(Game::rating).thenByDescending(Game::year).thenBy(Game::genre)
                    .thenByDescending(Game::id)
            )
        return listGames
    }
}