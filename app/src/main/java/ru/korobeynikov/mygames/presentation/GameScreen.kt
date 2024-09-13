package ru.korobeynikov.mygames.presentation

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun GameScreen(gameViewModel: GameViewModel, onNavigateToGenre: (GameViewModel) -> Unit) {
    val gameState by gameViewModel.gameScreenState
    var nameGame by remember { mutableStateOf(gameState.nameGame) }
    var ratingGame by remember { mutableStateOf(gameState.ratingGame) }
    var yearGame by remember { mutableStateOf(gameState.yearGame) }
    val genreGame by remember { mutableStateOf(gameState.genreGame) }
    var isSortGames by remember { mutableStateOf(gameState.isSortGames) }
    val interactionSource = remember {
        object : MutableInteractionSource {

            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                if (interaction is PressInteraction.Release) onNavigateToGenre.invoke(gameViewModel)
                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        EnterFields(
            nameGame = nameGame,
            ratingGame = ratingGame,
            yearGame = yearGame,
            genreGame = genreGame,
            interactionSource = interactionSource,
            onNameChange = { text ->
                nameGame = text
                gameViewModel.actionChangeName(text)
            },
            onRatingChange = { text ->
                ratingGame = text
                gameViewModel.actionChangeRating(text)
            },
            onYearChange = { text ->
                yearGame = text
                gameViewModel.actionChangeYear(text)
            }
        )

        ActionButtons(isSortGames) {
            isSortGames = !isSortGames
            gameViewModel.actionChangeSort(isSortGames)
        }

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Text(text = "Список игр")
        }
    }
}

@Composable
fun EnterFields(
    nameGame: String,
    ratingGame: String,
    yearGame: String,
    genreGame: String,
    interactionSource: MutableInteractionSource,
    onNameChange: (String) -> Unit,
    onRatingChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Название: ")
        OutlinedTextField(
            value = nameGame,
            modifier = Modifier.weight(1f),
            onValueChange = onNameChange
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Оценка: ")
        OutlinedTextField(
            value = ratingGame,
            modifier = Modifier.weight(1f),
            onValueChange = { text ->
                if (text.isDigitsOnly() && text.length < 3) onRatingChange.invoke(text)
            }
        )

        Text(text = "Год: ")
        OutlinedTextField(
            value = yearGame,
            modifier = Modifier.weight(1f),
            onValueChange = { text ->
                if (text.isDigitsOnly()) onYearChange.invoke(text)
            }
        )

        Text(text = "Жанр: ")
        OutlinedTextField(
            value = genreGame,
            readOnly = true,
            modifier = Modifier.weight(1.5f),
            interactionSource = interactionSource,
            onValueChange = {}
        )
    }
}

@Composable
fun ActionButtons(isSortGames: Boolean, onIsSortGamesChange: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(modifier = Modifier.weight(1f), onClick = {
            Log.d("myLogs", "Добавление игры")
        }) {
            Text("Добавить")
        }

        Button(modifier = Modifier.weight(1f), onClick = {
            Log.d("myLogs", "Изменение игры")
        }) {
            Text("Изменить")
        }

        Button(modifier = Modifier.weight(1f), onClick = {
            Log.d("myLogs", "Удаление игры")
        }) {
            Text("Удалить")
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(modifier = Modifier.weight(1f), onClick = {
            Log.d("myLogs", "Сохранение списка игр")
        }) {
            Text("Сохранить")
        }

        Button(modifier = Modifier.weight(1f), onClick = {
            Log.d("myLogs", "Загрузка списка игр")
        }) {
            Text("Загрузить")
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isSortGames, onCheckedChange = { onIsSortGamesChange.invoke() })
        Text("Сортировать")
    }
}