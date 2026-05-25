package ru.korobeynikov.mygames.data

class GameRepository(private val db: GameDatabase) {

    suspend fun getGamesFromDB() = db.gameDao().getAll()

    suspend fun addGameInDB(
        nameGame: String,
        ratingGame: Int,
        yearGame: Int,
        genreGame: String
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
        genreGame: String
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
}