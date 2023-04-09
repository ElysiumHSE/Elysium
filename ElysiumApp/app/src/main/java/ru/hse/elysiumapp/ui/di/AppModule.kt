package ru.hse.elysiumapp.ui.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.hse.elysiumapp.ui.network.AuthProvider
import ru.hse.elysiumapp.ui.network.Controller
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