package dev.jazalewski1.matchpoint.feature.match.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchControllerImpl

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides fun provideTennisMatchController(): MatchController = MatchControllerImpl()
}
