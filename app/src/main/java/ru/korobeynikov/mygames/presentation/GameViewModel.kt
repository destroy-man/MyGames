package ru.korobeynikov.mygames.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.korobeynikov.mygames.data.Game
import ru.korobeynikov.mygames.data.GameRepository

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {

    private val _gameScreenStateFlow =
        MutableStateFlow(GameScreenState("", "", "", "-", false, emptyList()))
    val gameScreenStateFlow: StateFlow<GameScreenState> = _gameScreenStateFlow
    private lateinit var gameScreenState: GameScreenState

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

    fun saveGames(path: String, onShowMessage: (String) -> Unit) {
        viewModelScope.launch {
            val message = gameRepository.saveGamesFromDB(path)
            onShowMessage.invoke(message)
        }
    }

    fun loadGames(path: String, onShowMessage: (String) -> Unit) {
        viewModelScope.launch {
            val message = gameRepository.loadGamesInDB(path)
            onShowMessage.invoke(message)
            getGames()
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