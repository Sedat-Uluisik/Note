package com.sedat.note.data.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sedat.note.R
import com.sedat.note.data.repository.NoteRepositoryImpl
import com.sedat.note.domain.database.Dao
import com.sedat.note.domain.database.NoteDatabase
import com.sedat.note.domain.repository.NoteRepository
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

    @Singleton
    @Provides
    fun initRepository(dao: Dao, @ApplicationContext context: Context): NoteRepository{
        return  NoteRepositoryImpl(dao = dao, context = context)
    }

    @Singleton
    @Provides
    fun initGlide(@ApplicationContext context: Context) =
        Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_downloading_32)
                    .error(R.drawable.ic_error_24)
            )
}