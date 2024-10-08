package ru.korobeynikov.mygames.presentation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    onShowMessage: (String) -> Unit,
    onNavigateToGenre: (GameViewModel) -> Unit,
    onSaveGames: () -> Unit,
    onLoadGames: () -> Unit,
) {
    val gameState by gameViewModel.gameScreenStateFlow.collectAsState()
    gameViewModel.setGameScreenState(gameState)
    val nameGame = gameState.nameGame
    val ratingGame = gameState.ratingGame
    val yearGame = gameState.yearGame
    val genreGame = gameState.genreGame
    val isSortGames = gameState.isSortGames
    val listGames =
        gameViewModel.filterListGames(nameGame, ratingGame, yearGame, genreGame, isSortGames)
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

    Column {
        EnterFields(
            nameGame = nameGame,
            ratingGame = ratingGame,
            yearGame = yearGame,
            genreGame = genreGame,
            interactionSource = interactionSource,
            onNameChange = { text ->
                gameViewModel.actionChangeName(text)
            },
            onRatingChange = { text ->
                gameViewModel.actionChangeRating(text)
            },
            onYearChange = { text ->
                gameViewModel.actionChangeYear(text)
            }
        )

        ActionButtons(
            nameGame,
            ratingGame,
            yearGame,
            genreGame,
            isSortGames,
            gameViewModel,
            onShowMessage,
            onSaveGames,
            onLoadGames
        ) {
            gameViewModel.actionChangeSort(!isSortGames)
        }

        Row(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        ) {
            val listGamesString = StringBuilder()
            var gameNumber = 1
            for (game in listGames) {
                listGamesString.append("$gameNumber. ${game.name} (${game.year}) = ${game.rating}\n")
                gameNumber++
            }
            Text(text = listGamesString.toString())
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
fun ActionButtons(
    nameGame: String,
    ratingGame: String,
    yearGame: String,
    genreGame: String,
    isSortGames: Boolean,
    gameViewModel: GameViewModel,
    onShowMessage: (String) -> Unit,
    onSaveGames: () -> Unit,
    onLoadGames: () -> Unit,
    onIsSortGamesChange: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(modifier = Modifier.weight(1f), onClick = {
            if (nameGame.isEmpty() || ratingGame.isEmpty() || yearGame.isEmpty() || genreGame == "-")
                onShowMessage.invoke("Для добавления игры необходимо указать название, оценку, год и жанр игры")
            else if (ratingGame.toInt() < 1 || ratingGame.toInt() > 10)
                onShowMessage.invoke("Оценка должна принимать значение в интервале от 1 до 10")
            else gameViewModel.addGame(nameGame, ratingGame, yearGame, genreGame, onShowMessage)
        }) {
            Text("Добавить")
        }

        Button(modifier = Modifier.weight(1f), onClick = {
            if (nameGame.isEmpty() || yearGame.isEmpty())
                onShowMessage.invoke("Для обновления данных по игре необходимо указать название и год игры")
            else if (ratingGame.isNotEmpty() && (ratingGame.toInt() < 1 || ratingGame.toInt() > 10))
                onShowMessage.invoke("Оценка должна принимать значение в интервале от 1 до 10")
            else gameViewModel.changeGame(nameGame, ratingGame, yearGame, genreGame, onShowMessage)
        }) {
            Text("Изменить")
        }

        Button(modifier = Modifier.weight(1f), onClick = {
            if (nameGame.isEmpty() || yearGame.isEmpty())
                onShowMessage.invoke("Для удаления игры необходимо указать название и год игры")
            else gameViewModel.deleteGame(nameGame, yearGame, onShowMessage)
        }) {
            Text("Удалить")
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(modifier = Modifier.weight(1f), onClick = onSaveGames) {
            Text("Сохранить")
        }

        Button(modifier = Modifier.weight(1f), onClick = onLoadGames) {
            Text("Загрузить")
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isSortGames, onCheckedChange = { onIsSortGamesChange.invoke() })
        Text("Сортировать")
    }
}