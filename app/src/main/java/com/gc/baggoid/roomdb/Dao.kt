package com.gc.baggoid.roomdb

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gc.baggoid.models.GameState
import androidx.room.Dao

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameState(gameSte: GameState)

    @Query("SELECT * FROM game_state LIMIT 1") //"SELECT * FROM game_state LIMIT 1" or "SELECT * FROM game_state WHERE id=99"
    fun getGameState(): GameState?
}