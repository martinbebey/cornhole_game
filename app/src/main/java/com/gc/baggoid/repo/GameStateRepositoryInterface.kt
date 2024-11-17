package com.gc.baggoid.repo

import com.gc.baggoid.models.GameState

interface GameStateRepositoryInterface {
    suspend fun insertGameState(gameState: GameState)
    suspend fun deleteGameState(gameState: GameState)
    fun getGameState(): GameState
}