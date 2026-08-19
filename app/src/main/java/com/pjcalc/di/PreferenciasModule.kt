package com.pjcalc.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.pjcalc.data.prefs.PreferenciasDataStore
import com.pjcalc.data.prefs.PreferenciasRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenciasModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("preferencias")
        }

    @Provides
    @Singleton
    fun providePreferenciasRepository(
        dataStore: DataStore<Preferences>
    ): PreferenciasRepository = PreferenciasDataStore(dataStore)
}
