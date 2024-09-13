package ru.korobeynikov.mygames.presentation

import ru.korobeynikov.mygames.data.Game

data class GameScreenState(
    val nameGame: String,
    val ratingGame: String,
    val yearGame: String,
    val genreGame: String,
    val isSortGames: Boolean,
    val listGames: List<Game>,
)
