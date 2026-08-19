package com.pjcalc.di

import android.content.Context
import androidx.room.Room
import com.pjcalc.data.local.PjDatabase
import com.pjcalc.data.local.RegistroMesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePjDatabase(@ApplicationContext context: Context): PjDatabase =
        Room.databaseBuilder(context, PjDatabase::class.java, "pjcalc.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideRegistroMesDao(db: PjDatabase): RegistroMesDao = db.registroMesDao()
}
