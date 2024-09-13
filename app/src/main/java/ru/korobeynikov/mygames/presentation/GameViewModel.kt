package ru.korobeynikov.mygames.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

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
}