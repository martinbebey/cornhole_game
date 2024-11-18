package com.gc.baggoid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gc.baggoid.models.*
import com.gc.baggoid.repo.BaggoidRepository
import com.gc.baggoid.repo.GameStateRepositoryInterface
import com.gc.baggoid.repo.Graph
import com.gc.baggoid.views.FieldView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class GameViewModel @Inject constructor(
        private val repository: GameStateRepositoryInterface
    ) : ViewModel(), FieldView.BagListener {
    // LiveData to hold the game state
    private val _gameState: MutableLiveData<GameState> = MutableLiveData()
    val gameState: LiveData<GameState> = _gameState

    private val _currentTeam = MutableLiveData<Team>(Team.RED)
    val currentTeam: LiveData<Team> get() = _currentTeam

    // Flag to indicate if the game state is being loaded from the database
    private val _isGameStateLoading = MutableLiveData<Boolean>(true)
    val isGameStateLoading: LiveData<Boolean> = _isGameStateLoading

    // Initialize the game state by loading it from the database or creating a new one
    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Try to load the game state from the database
            val savedGameState = repository.getGameState()
            _isGameStateLoading.postValue(false)

            // If there's a saved game state, use it; otherwise, create a new one
            val gameStateToUse = savedGameState ?: GameState.newGame()

            // Post the game state to LiveData
            _gameState.postValue(gameStateToUse)

            // Set the current team based on the saved game state, or default to RED
            val savedCurrentTeam = savedGameState?.currentTeam ?: Team.RED
            _currentTeam.postValue(savedCurrentTeam)
        }
    }

    // Derived LiveData for UI updates
    val clearBags: LiveData<Boolean> = Transformations.map(gameState) { gameState ->
        if (_isGameStateLoading.value == true) {
            false  // Don't clear bags while loading
        } else {
            gameState.currentRound.isRoundNew  // Clear bags only if the round is new
        }
    }

    val roundOverState: LiveData<RoundOverState?> = Transformations.map(gameState) { gameState ->
        if (gameState.currentRound.isRoundOver) {
            RoundOverState(
                gameState.currentRoundNumber,
                gameState.currentRound.roundWinner,
                gameState.currentRound.roundDelta,
            )
        } else null
    }

    val gameOverState: LiveData<GameOverState?> = Transformations.map(gameState) { gameState ->
        gameState.gameWinner?.let { gameWinner ->
            GameOverState(
                gameWinner,
                gameState.redTeamTotalScore,
                gameState.blueTeamTotalScore,
            )
        }
    }

    // Handle bag moved event (updates game state)
    override fun onBagMoved(bagMovedEvent: BagMovedEvent) {
        _gameState.value = _gameState.value?.processEvent(bagMovedEvent)

        saveGameStateToDatabase(_gameState.value)
    }

    fun changeRulesMode(newMode: RulesMode) {
        _gameState.value = _gameState.value?.copy(rulesMode = newMode)
    }

    // Start a new game, resetting the state
    fun startNewGame() {
        val newGameState = GameState.newGame()
        _gameState.value = newGameState
        _currentTeam.value = Team.RED
        saveGameStateToDatabase(newGameState) // Save the new game state to the database
    }

    // Start a new round, updating the current game state
    fun startNewRound() {
        _gameState.value = _gameState.value?.newRound()
        saveGameStateToDatabase(_gameState.value) // Save the updated game state
    }

    // Save the game state to the database
    private fun saveGameStateToDatabase(gameState: GameState?) {
        gameState?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.saveGameState(it)
            }
        }
    }
}