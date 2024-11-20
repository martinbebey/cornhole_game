package com.gc.baggoid

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gc.baggoid.models.BagMovedEvent
import com.gc.baggoid.models.BagStatus
import com.gc.baggoid.models.GameState
import com.gc.baggoid.models.RulesMode
import com.gc.baggoid.models.Team
import com.gc.baggoid.repo.GameStateRepositoryInterface
import com.gc.baggoid.viewmodel.GameViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GameVMTest {

    // Inject the necessary dependencies using Hilt
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var repository: GameStateRepositoryInterface

    // LiveData observer to check the changes
    @Mock
    lateinit var gameStateObserver: Observer<GameState>

    @Mock
    lateinit var currentTeamObserver: Observer<Team>

    @Mock
    lateinit var rulesModeObserver: Observer<RulesMode>

    @Mock
    private lateinit var viewModel: GameViewModel

    // Rule to allow LiveData to execute in the background
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() = runBlocking{
        // Initialize Hilt injection
        // You should have a Dagger Hilt test rule here to inject dependencies
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
    fun testInitialGameState() {
        // Verify initial state (this can be customized based on your initial setup)
        verify(gameStateObserver).onChanged(any())
        verify(currentTeamObserver).onChanged(Team.RED)
        verify(rulesModeObserver).onChanged(RulesMode.SIMPLE)
    }

    @Test
    fun testStartNewGame() = runBlocking{
        // Call the startNewGame() method
        viewModel.startNewGame()

        // Verify that the game state is reset
        verify(gameStateObserver).onChanged(any())
        verify(currentTeamObserver).onChanged(Team.RED)

        // Optionally, verify that the saveGameStateToDatabase was called
        verify(repository).saveGameState(any())
    }

    @Test
    fun testStartNewRound() = runBlocking{
        // Call the startNewRound() method
        viewModel.startNewRound()

        // Verify that the game state is updated
        verify(gameStateObserver).onChanged(any())

        // Optionally, verify that the saveGameStateToDatabase was called
        verify(repository).saveGameState(any())
    }

    @Test
    fun testLoadRulesMode() {
        // Simulate loading rules mode
        viewModel.loadRulesMode()

        // Verify that the rules mode is loaded correctly
        verify(rulesModeObserver).onChanged(RulesMode.SIMPLE)
    }

    @Test
    fun testOnBagMoved() = runBlocking {
        // Create a mock event for bag moved
        val mockEvent = BagMovedEvent(BagStatus.IN_HAND, BagStatus.ON_BOARD, Team.RED)

        // Call the onBagMoved method
        viewModel.onBagMoved(mockEvent)

        // Verify that the game state was updated
        verify(gameStateObserver).onChanged(any())

        // Optionally, verify that the saveGameStateToDatabase was called
        verify(repository).saveGameState(any())
    }
}
