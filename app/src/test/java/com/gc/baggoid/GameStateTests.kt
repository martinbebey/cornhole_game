package com.gc.baggoid

import com.gc.baggoid.models.*
import org.junit.Assert.*
import org.junit.Test

class GameStateTests {

    @Test
    fun testNewGameState() {
        val gameState = GameState.newGame()

        assertEquals(BAGS_PER_ROUND, gameState.currentRound.redTeamBagsRemaining)
        assertEquals(BAGS_PER_ROUND, gameState.currentRound.blueTeamBagsRemaining)
        assertEquals(0, gameState.currentRound.redTeamRoundScore)
        assertEquals(0, gameState.currentRound.blueTeamRoundScore)
        assertEquals(0, gameState.redTeamTotalScore)
        assertEquals(0, gameState.blueTeamTotalScore)
    }

    @Test
    fun testImmutable() {
        val before = GameState.newGame()
        val after = before.currentRound.modifyScore(Team.RED, 3)

        assertEquals(0, before.currentRound.redTeamRoundScore)
        assertEquals(3, after.redTeamRoundScore)
    }

    @Test
    fun testGameWinner() {
        var gameState = GameState.newGame()

        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.newRound(RulesMode.SIMPLE)
        assertEquals(null, gameState.gameWinner(RulesMode.SIMPLE))

        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.newRound(RulesMode.SIMPLE)
        assertEquals(Team.RED, gameState.gameWinner(RulesMode.SIMPLE))
    }

    @Test
    fun testRoundOver() {
        var gameState = GameState.newGame()
        assertFalse(gameState.currentRound.isRoundOver)

        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        assertFalse(gameState.currentRound.isRoundOver)
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        assertTrue(gameState.currentRound.isRoundOver)
    }

    @Test
    fun testRoundScore() {
        var gameState = GameState.newGame()

        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))

        assertEquals(12, gameState.currentRound.redTeamRoundScore)
        assertEquals(0, gameState.currentRound.blueTeamRoundScore)
    }

    @Test
    fun testTotalScore() {
        var gameState = GameState.newGame()

        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        gameState = gameState.processEvent(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))

        assertEquals(0, gameState.redTeamTotalScore)
        assertEquals(0, gameState.blueTeamTotalScore)

        gameState = gameState.newRound(RulesMode.SIMPLE)

        assertEquals(12, gameState.redTeamTotalScore)
        assertEquals(0, gameState.blueTeamTotalScore)
    }
}
