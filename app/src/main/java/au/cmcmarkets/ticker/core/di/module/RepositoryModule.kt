package au.cmcmarkets.ticker.core.di.module

import au.cmcmarkets.ticker.data.repository.BitCoinRepositoryImpl
import au.cmcmarkets.ticker.data.repository.IBitCoinRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    internal abstract fun bindBitCoinRepository(repository: BitCoinRepositoryImpl): IBitCoinRepository
}