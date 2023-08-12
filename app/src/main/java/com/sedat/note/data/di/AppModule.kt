package com.sedat.note.data.di

import android.content.Context
import androidx.room.Room
import com.sedat.note.domain.database.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun initRoomDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "NoteDB"
        ).build()

    @Singleton
    @Provides
    fun initDao(database: NoteDatabase) = database.dao()
}