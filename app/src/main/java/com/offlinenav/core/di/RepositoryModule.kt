package com.offlinenav.core.di

import com.offlinenav.data.repository.SavedPlaceRepositoryImpl
import com.offlinenav.data.repository.SettingsRepositoryImpl
import com.offlinenav.data.repository.TripRepositoryImpl
import com.offlinenav.data.routing.RouteRepositoryImpl
import com.offlinenav.data.search.SearchRepositoryImpl
import com.offlinenav.domain.repository.*
import com.offlinenav.download.repository.DownloadRepositoryImpl
import com.offlinenav.gps.location.LocationManagerImpl
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
    abstract fun bindLocationRepository(impl: LocationManagerImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(impl: RouteRepositoryImpl): RouteRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository

    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository

    @Binds
    @Singleton
    abstract fun bindSavedPlaceRepository(impl: SavedPlaceRepositoryImpl): SavedPlaceRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
