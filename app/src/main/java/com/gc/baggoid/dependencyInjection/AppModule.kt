package com.gc.baggoid.dependencyInjection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.gc.baggoid.repo.BaggoidRepository
import com.gc.baggoid.repo.GameStateRepositoryInterface
import com.gc.baggoid.roomdb.CornHoleDB
import com.gc.baggoid.roomdb.Dao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * for dependency injection
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide the Context as a dependency
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    // Provide the Room database instance
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): CornHoleDB {
        return CornHoleDB.getDatabase(context)  // Call the getDatabase() method from CornHoleDB
    }

    // Provide the GameStateDao from the AppDatabase
    @Provides
    @Singleton
    fun provideGameStateDao(appDatabase: CornHoleDB): Dao {
        return appDatabase.gameStateDao()
    }

    // Provide the BaggoidRepository, which depends on GameStateDao
    @Provides
    @Singleton
    fun provideBaggoidRepository(gameStateDao: Dao): GameStateRepositoryInterface {
        return BaggoidRepository(gameStateDao)
    }
}