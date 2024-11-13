package com.herra_org.heraclient.di

import com.herra_org.heraclient.data.remote.repository.HomeRepositoryImpl
import com.herra_org.heraclient.domain.repository.HomeRepository
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository
}