package ru.korobeynikov.mygames.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    var rating: Int,
    var year: Int,
    var genre: String,
)
