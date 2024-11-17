package com.gc.baggoid.repo

import com.gc.baggoid.models.GameState
import com.gc.baggoid.roomdb.Dao
import javax.inject.Inject

class BaggoidRepository @Inject constructor(
    private val gameStateDao: Dao,
): GameStateRepositoryInterface {
    override suspend fun insertGameState(gameState: GameState) {
        gameStateDao.insertGameState(gameState)
    }
    override suspend fun deleteGameState(gameState: GameState) {
        gameStateDao.deleteGameState(gameState)
    }
    override fun getGameState(): GameState {
        return gameStateDao.getGameState()
    }
}