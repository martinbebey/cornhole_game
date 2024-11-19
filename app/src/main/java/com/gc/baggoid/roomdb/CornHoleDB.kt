package com.gc.baggoid.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gc.baggoid.models.GameState
import com.gc.baggoid.utils.RoundStateConverter

@Database(entities = [GameState::class], version = 7)
@TypeConverters(RoundStateConverter::class)
abstract class CornHoleDB: RoomDatabase() {
    abstract fun gameStateDao(): Dao

    companion object {
        private const val DB_NAME = "game_database"

        // Initialize the Room database with a context
        @Volatile
        private var INSTANCE: CornHoleDB? = null

        fun getDatabase(context: Context): CornHoleDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use the applicationContext to avoid memory leaks
                    CornHoleDB::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration() // This is where the destructive migration happens
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Define the migration from version 1 to version 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the 'currentTeam' column to the 'game_state' table
                database.execSQL("ALTER TABLE game_state ADD COLUMN current_team INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}