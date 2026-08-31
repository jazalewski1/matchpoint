package dev.jazalewski1.matchpoint.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jazalewski1.matchpoint.core.data.MatchRepository
import dev.jazalewski1.matchpoint.core.data.MemoryMatchRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds @Singleton abstract fun bindMatchRepository(impl: MemoryMatchRepository): MatchRepository
}
