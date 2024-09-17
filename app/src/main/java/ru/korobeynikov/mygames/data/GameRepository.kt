package ru.korobeynikov.mygames.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class GameRepository(private val db: GameDatabase) {

    suspend fun getGamesFromDB() = db.gameDao().getAll()

    suspend fun addGameInDB(
        nameGame: String,
        ratingGame: Int,
        yearGame: Int,
        genreGame: String,
    ): String {
        val game = db.gameDao().getGame(nameGame, yearGame)
        return if (game == null) {
            db.gameDao().insert(
                Game(
                    name = nameGame,
                    rating = ratingGame,
                    year = yearGame,
                    genre = genreGame
                )
            )
            "Игра успешно добавлена"
        } else "Данная игра уже добавлена"
    }

    suspend fun changeGameInDB(
        nameGame: String,
        ratingGame: String,
        yearGame: String,
        genreGame: String,
    ): String {
        val game = db.gameDao().getGame(nameGame, yearGame.toInt())
        return if (game != null) {
            var isGameChange = false
            if (ratingGame.isNotEmpty()) {
                game.rating = ratingGame.toInt()
                isGameChange = true
            }
            if (genreGame != "-") {
                game.genre = genreGame
                if (!isGameChange) isGameChange = true
            }
            if (isGameChange) {
                db.gameDao().update(game)
                "Информация об игре успешно обновлена"
            } else "Для обновления информации об игре необходимо указать оценку или жанр игры"
        } else "Данной игры нет в базе данных"
    }

    suspend fun deleteGameInDB(nameGame: String, yearGame: Int): String {
        val game = db.gameDao().getGame(nameGame, yearGame)
        return if (game != null) {
            db.gameDao().delete(game)
            "Игра успешно удалена"
        } else "Данной игры нет в базе данных"
    }

    suspend fun saveGamesFromDB(path: String): String {
        val directory = File(path, "MyGames")
        if (!directory.exists()) directory.mkdirs()
        val gamesFile = File("${directory.path}/games.txt")
        return try {
            withContext(Dispatchers.IO) {
                val listGames = getGamesFromDB()
                if (listGames.isNotEmpty()) {
                    val writer = BufferedWriter(FileWriter(gamesFile))
                    for (game in listGames) {
                        writer.write("${game.name};${game.rating};${game.year};${game.genre}")
                        writer.newLine()
                    }
                    writer.close()
                    "Сохранение в файл успешно завершено"
                } else "Нет данных для сохранения"
            }
        } catch (e: IOException) {
            "Произошла ошибка при сохранении в файл"
        }
    }

    suspend fun loadGamesInDB(path: String): String {
        val directory = File(path, "MyGames")
        if (!directory.exists()) directory.mkdirs()
        val gamesFile = File("${directory.path}/games.txt")
        return try {
            withContext(Dispatchers.IO) {
                val reader = BufferedReader(FileReader(gamesFile))
                var line = reader.readLine()
                while (line != null) {
                    val nameGame = line.split(";")[0]
                    val ratingGame = line.split(";")[1].toInt()
                    val yearGame = line.split(";")[2].toInt()
                    val genreGame = line.split(";")[3]
                    addGameInDB(nameGame, ratingGame, yearGame, genreGame)
                    line = reader.readLine()
                }
                reader.close()
                "Данные из файла успешно загружены"
            }
        } catch (e: IOException) {
            "Произошла ошибка при загрузке данных из файла"
        }
    }
}