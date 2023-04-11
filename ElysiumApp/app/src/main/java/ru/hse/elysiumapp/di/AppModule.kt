package ru.hse.elysiumapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.hse.elysiumapp.network.AuthProvider
import ru.hse.elysiumapp.network.Controller
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideController() = Controller()

    @Singleton
    @Provides
    fun provideAuthProvider() = AuthProvider()
}