package com.gc.baggoid

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.gc.baggoid.models.*
import com.gc.baggoid.repo.GameStateRepositoryInterface
import com.gc.baggoid.viewmodel.GameViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import javax.inject.Inject

class GameViewModelTest {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var repository: GameStateRepositoryInterface

    @Mock
    lateinit var gameStateObserver: Observer<GameState>

    @Mock
    lateinit var currentTeamObserver: Observer<Team>

    @Mock
    lateinit var rulesModeObserver: Observer<RulesMode>

    private lateinit var viewModel: GameViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() = runBlocking{
        // Initialize Hilt injection
        hiltRule.inject()

        // Create the ViewModel with injected dependencies
        viewModel = GameViewModel(context, repository)

        // Mock repository behavior for getGameState() and saveGameState()
        `when`(repository.getGameState()).thenReturn(GameState.newGame())

        // Observe LiveData
        viewModel.gameState.observeForever(gameStateObserver)
        viewModel.currentTeam.observeForever(currentTeamObserver)
        viewModel.rulesMode.observeForever(rulesModeObserver)
    }

    @Test
    fun testNoEvents() {

        assertEquals(1, viewModel.gameState.value?.currentRoundNumber)
        assertEquals(BAGS_PER_ROUND, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(BAGS_PER_ROUND, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(0, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.redTeamTotalScore)
        assertEquals(0, viewModel.gameState.value?.blueTeamTotalScore)
        assertEquals(true, viewModel.gameState.value?.currentRound?.isRoundNew)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundOver)
    }

    @Test
    fun testResetGame() {

        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.BLUE))

        viewModel.startNewGame()
        assertEquals(1, viewModel.gameState.value?.currentRoundNumber)
        assertEquals(BAGS_PER_ROUND, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(BAGS_PER_ROUND, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(0, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.redTeamTotalScore)
        assertEquals(0, viewModel.gameState.value?.blueTeamTotalScore)
        assertEquals(true, viewModel.gameState.value?.currentRound?.isRoundNew)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundOver)
    }

    @Test
    fun testDragBag() {

        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.BLUE))

        assertEquals(3, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(3, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(1, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(1, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)

        // Start Dragging
        viewModel.onBagMoved(BagMovedEvent(BagStatus.ON_BOARD, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.ON_BOARD, BagStatus.OFF_BOARD, Team.BLUE))

        assertEquals(3, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(3, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(3, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)
    }

    @Test
    fun testNewRound() {
        val roundOverState = mockk<Observer<RoundOverState?>>(relaxed = true)
        viewModel.roundOverState.observeForever(roundOverState)

        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.BLUE))

        assertEquals(2, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(2, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(2, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(2, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.redTeamTotalScore)
        assertEquals(0, viewModel.gameState.value?.blueTeamTotalScore)
        assertEquals(1, viewModel.gameState.value?.currentRoundNumber)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundNew)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundOver)

        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.BLUE))

        verify(exactly = 1) { roundOverState.onChanged(RoundOverState(1, null, 0)) }
        assertEquals(0, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(8, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(8, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.redTeamTotalScore)
        assertEquals(0, viewModel.gameState.value?.blueTeamTotalScore)
        assertEquals(1, viewModel.gameState.value?.currentRoundNumber)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundNew)
        assertEquals(true, viewModel.gameState.value?.currentRound?.isRoundOver)

        viewModel.startNewRound()
        assertEquals(2, viewModel.gameState.value?.currentRoundNumber)
        assertEquals(true, viewModel.gameState.value?.currentRound?.isRoundNew)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundOver)
    }

    @Test
    fun testWinning() {

        // Round 1
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))

        assertEquals(0, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(12, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.redTeamTotalScore)
        assertEquals(0, viewModel.gameState.value?.blueTeamTotalScore)
        assertEquals(1, viewModel.gameState.value?.currentRoundNumber)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundNew)
        assertEquals(true, viewModel.gameState.value?.currentRound?.isRoundOver)

        assertEquals(null, viewModel.gameState.value?.gameWinner(rulesMode = RulesMode.SIMPLE))

        // Round 2
        viewModel.startNewRound()

        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))

        assertEquals(0, viewModel.gameState.value?.currentRound?.redTeamBagsRemaining)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamBagsRemaining)
        assertEquals(12, viewModel.gameState.value?.currentRound?.redTeamRoundScore)
        assertEquals(0, viewModel.gameState.value?.currentRound?.blueTeamRoundScore)
        assertEquals(12, viewModel.gameState.value?.redTeamTotalScore)
        assertEquals(0, viewModel.gameState.value?.blueTeamTotalScore)
        assertEquals(2, viewModel.gameState.value?.currentRoundNumber)
        assertEquals(false, viewModel.gameState.value?.currentRound?.isRoundNew)
        assertEquals(true, viewModel.gameState.value?.currentRound?.isRoundOver)

        viewModel.startNewRound()
        assertEquals(Team.RED, viewModel.gameState.value?.gameWinner(RulesMode.SIMPLE))

        val gameOverState = mockk<Observer<GameOverState?>>(relaxed = true)
        viewModel.gameOverState.observeForever(gameOverState)

        verifySequence {
            gameOverState.onChanged(GameOverState(Team.RED, 24, 0))
        }
    }

    @Test
    fun testClearBags() {

        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.IN_HOLE, Team.RED))
        viewModel.onBagMoved(BagMovedEvent(BagStatus.IN_HAND, BagStatus.OFF_BOARD, Team.BLUE))

        viewModel.startNewRound()

        val clearBagsObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.clearBags.observeForever(clearBagsObserver)

        verify(exactly = 1) {
            clearBagsObserver.onChanged(true)
        }
    }
}