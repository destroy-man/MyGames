package ru.korobeynikov.mygames.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GameDao {

    @Query("SELECT * FROM game")
    suspend fun getAll(): List<Game>

//    @Query("SELECT * FROM game ORDER BY rating, year, genre, name")
//    suspend fun getSortedAll():List<Game>

    @Query("SELECT * FROM game WHERE name = :nameGame AND year = :yearGame")
    suspend fun getGame(nameGame: String, yearGame: Int): Game?

    @Insert
    suspend fun insert(game: Game)

    @Update
    suspend fun update(game: Game)

    @Delete
    suspend fun delete(game: Game)
}