package dev.jazalewski1.matchpoint.feature.match.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import dev.jazalewski1.matchpoint.core.data.MatchRepository
import dev.jazalewski1.matchpoint.core.data.MemoryMatchRepository
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchControllerImpl

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides fun provideTennisMatchController(): MatchController = MatchControllerImpl()
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides fun provideMatchRepository(): MatchRepository = MemoryMatchRepository()
}