package com.gc.baggoid.roomdb

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gc.baggoid.models.GameState
import androidx.room.Dao

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameState(gameSte: GameState)

    @Delete
    suspend fun deleteGameState(gameSte: GameState)

    @Query("SELECT * FROM game_state WHERE id=99") //OR "SELECT * FROM game_state LIMIT 1"
    fun getGameState(): GameState?
}