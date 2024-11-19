package com.gc.baggoid.repo

import com.gc.baggoid.models.GameState

interface GameStateRepositoryInterface {

    suspend fun saveGameState(gameState: GameState)

    suspend fun getGameState(): GameState?
}