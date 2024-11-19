package com.gc.baggoid.roomdb

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gc.baggoid.models.GameState
import com.gc.baggoid.utils.RoundStateConverter

@Database(
    entities = [GameState::class],
    version = 7,
)
@TypeConverters(RoundStateConverter::class)
abstract class CornHoleDB: RoomDatabase() {
    abstract fun gameStateDao(): Dao

    companion object {
        private const val DB_NAME = "cornhole_database"

        // Init room db
        @Volatile
        private var INSTANCE: CornHoleDB? = null

        fun getDatabase(context: Context): CornHoleDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CornHoleDB::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_7_8)
                    .fallbackToDestructiveMigration() // destructive db migration
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Define the migration from version 7 to version 8
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // Add the 'currentTeam' column to the 'game_state' table
                database.execSQL("ALTER TABLE game_state ADD COLUMN current_team INTEGER NOT NULL DEFAULT 0")

            }
        }
    }
}