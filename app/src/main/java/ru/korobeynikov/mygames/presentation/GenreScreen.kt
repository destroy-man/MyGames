package ru.korobeynikov.mygames.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GenreScreen(gameViewModel: GameViewModel, onNavigateToGame: (GameViewModel) -> Unit) {
    val genresList = gameViewModel.getGenres()
    LazyColumn {
        items(genresList.count()) { index ->
            GenreItem(gameViewModel, genresList[index], onNavigateToGame)
        }
    }
}

@Composable
fun GenreItem(
    gameViewModel: GameViewModel,
    genre: String,
    onNavigateToGame: (GameViewModel) -> Unit,
) {
    Text(
        text = genre,
        modifier = Modifier
            .fillMaxSize()
            .border(width = 1.dp, color = Color.Black)
            .padding(16.dp)
            .clickable(onClick = {
                gameViewModel.actionChangeGenre(genre)
                onNavigateToGame.invoke(gameViewModel)
            }),
    )
}