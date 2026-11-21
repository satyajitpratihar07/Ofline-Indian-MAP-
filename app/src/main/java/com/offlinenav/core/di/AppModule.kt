package com.offlinenav.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.offlinenav.core.data.local.OfflineNavDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// DataStore instance — created as a top-level extension on Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "offlinenav_preferences"
)

/**
 * Hilt application-scoped dependency injection module.
 *
 * Provides singleton instances of:
 * - [OfflineNavDatabase] (Room)
 * - [OkHttpClient] (networking)
 * - [Gson] (JSON serialization)
 * - [DataStore<Preferences>] (settings persistence)
 * - DAO instances from the database
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ─────────────────────────────────────────
    // Database
    // ─────────────────────────────────────────

    @Provides
    @Singleton
    fun provideOfflineNavDatabase(
        @ApplicationContext context: Context
    ): OfflineNavDatabase {
        return Room.databaseBuilder(
            context,
            OfflineNavDatabase::class.java,
            OfflineNavDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigrationOnDowngrade()
            .addMigrations(*OfflineNavDatabase.MIGRATIONS)
            .build()
    }

    @Provides
    @Singleton
    fun provideSavedPlaceDao(database: OfflineNavDatabase) =
        database.savedPlaceDao()

    @Provides
    @Singleton
    fun provideNavigationHistoryDao(database: OfflineNavDatabase) =
        database.navigationHistoryDao()

    @Provides
    @Singleton
    fun provideMapRegionDao(database: OfflineNavDatabase) =
        database.mapRegionDao()

    // ─────────────────────────────────────────
    // Network — OkHttpClient
    // ─────────────────────────────────────────

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag("OkHttp").d(message)
        }.apply {
            level = if (com.offlinenav.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Add common headers to every request
                val request = chain.request().newBuilder()
                    .header("User-Agent", "OfflineNav/${com.offlinenav.BuildConfig.VERSION_NAME}")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .retryOnConnectionFailure(true)
            .build()
    }

    // ─────────────────────────────────────────
    // Serialization — Gson
    // ─────────────────────────────────────────

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .setPrettyPrinting()
            .create()
    }

    // ─────────────────────────────────────────
    // Preferences — DataStore
    // ─────────────────────────────────────────

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    // ─────────────────────────────────────────
    // Application Context
    // ─────────────────────────────────────────

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context
}
