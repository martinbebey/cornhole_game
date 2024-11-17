package com.gc.baggoid.repo

import androidx.lifecycle.LiveData
import com.gc.baggoid.models.GameState

interface GameStateRepositoryInterface {

    suspend fun saveGameState(gameState: GameState)

    suspend fun deleteGameState(gameState: GameState)

    fun getGameState(): GameState?

}