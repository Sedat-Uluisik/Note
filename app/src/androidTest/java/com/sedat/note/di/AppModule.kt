package com.sedat.note.di

import android.content.Context
import androidx.room.Room
import com.sedat.note.domain.database.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Named("TestDatabase") //Named ile gerçek db yi değil memory de oluşturulan test db yi inject eder.
    fun injectInMemoryRoom(@ApplicationContext context: Context) = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).allowMainThreadQueries().build()

}