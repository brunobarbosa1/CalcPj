package com.pjcalc.di

import com.pjcalc.data.repository.RegistroMesRepository
import com.pjcalc.data.repository.RegistroMesRepositoryRoom
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRegistroMesRepository(
        impl: RegistroMesRepositoryRoom
    ): RegistroMesRepository
}
