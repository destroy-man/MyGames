package ru.korobeynikov.mygames.di

import androidx.room.Room
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.korobeynikov.mygames.data.GameDatabase
import ru.korobeynikov.mygames.data.GameRepository
import ru.korobeynikov.mygames.presentation.GameViewModel

val gameViewModelModule = module {
    single { Room.databaseBuilder(get(), GameDatabase::class.java, "gamesDatabase").build() }
    single { GameRepository(get()) }
    viewModel { GameViewModel(get()) }
}