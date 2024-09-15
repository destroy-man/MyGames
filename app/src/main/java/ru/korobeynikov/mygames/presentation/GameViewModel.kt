package ru.korobeynikov.mygames.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.korobeynikov.mygames.data.Game
import ru.korobeynikov.mygames.data.GameRepository

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {

    private val _gameScreenState =
        mutableStateOf(GameScreenState("", "", "", "-", false, emptyList()))
    val gameScreenState: State<GameScreenState> = _gameScreenState

    fun actionChangeName(name: String) {
        val gameScreenStateValue by gameScreenState
        _gameScreenState.value = GameScreenState(
            name,
            gameScreenStateValue.ratingGame,
            gameScreenStateValue.yearGame,
            gameScreenStateValue.genreGame,
            gameScreenStateValue.isSortGames,
            gameScreenStateValue.listGames
        )
    }

    fun actionChangeRating(rating: String) {
        val gameScreenStateValue by gameScreenState
        _gameScreenState.value = GameScreenState(
            gameScreenStateValue.nameGame,
            rating,
            gameScreenStateValue.yearGame,
            gameScreenStateValue.genreGame,
            gameScreenStateValue.isSortGames,
            gameScreenStateValue.listGames
        )
    }

    fun actionChangeYear(year: String) {
        val gameScreenStateValue by gameScreenState
        _gameScreenState.value = GameScreenState(
            gameScreenStateValue.nameGame,
            gameScreenStateValue.ratingGame,
            year,
            gameScreenStateValue.genreGame,
            gameScreenStateValue.isSortGames,
            gameScreenStateValue.listGames
        )
    }

    fun actionChangeGenre(genre: String) {
        val gameScreenStateValue by gameScreenState
        _gameScreenState.value = GameScreenState(
            gameScreenStateValue.nameGame,
            gameScreenStateValue.ratingGame,
            gameScreenStateValue.yearGame,
            genre,
            gameScreenStateValue.isSortGames,
            gameScreenStateValue.listGames
        )
    }

    fun actionChangeSort(isSort: Boolean) {
        val gameScreenStateValue by gameScreenState
        _gameScreenState.value = GameScreenState(
            gameScreenStateValue.nameGame,
            gameScreenStateValue.ratingGame,
            gameScreenStateValue.yearGame,
            gameScreenStateValue.genreGame,
            isSort,
            gameScreenStateValue.listGames
        )
    }

    private fun actionChangeListGames(listGames: List<Game>) {
        val gameScreenStateValue by gameScreenState
        _gameScreenState.value = GameScreenState(
            gameScreenStateValue.nameGame,
            gameScreenStateValue.ratingGame,
            gameScreenStateValue.yearGame,
            gameScreenStateValue.genreGame,
            gameScreenStateValue.isSortGames,
            listGames
        )
    }

    fun getGames() {
        viewModelScope.launch {
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
        viewModelScope.launch {
            val message = gameRepository.addGameInDB(
                nameGame,
                ratingGame.toInt(),
                yearGame.toInt(),
                genreGame
            )
            onShowMessage.invoke(message)
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
        viewModelScope.launch {
            val message = gameRepository.changeGameInDB(nameGame, ratingGame, yearGame, genreGame)
            onShowMessage.invoke(message)
            getGames()
        }
    }

    fun deleteGame(nameGame: String, yearGame: String, onShowMessage: (String) -> Unit) {
        viewModelScope.launch {
            val message = gameRepository.deleteGameInDB(nameGame, yearGame.toInt())
            onShowMessage.invoke(message)
            getGames()
        }
    }
}