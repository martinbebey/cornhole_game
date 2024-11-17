package com.gc.baggoid.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gc.baggoid.models.GameState
import com.gc.baggoid.utils.RoundStateConverter

@Database(entities = [GameState::class], version = 1)
@TypeConverters(RoundStateConverter::class)
abstract class CornHoleDB: RoomDatabase() {
    abstract fun GameStateDao(): Dao
}