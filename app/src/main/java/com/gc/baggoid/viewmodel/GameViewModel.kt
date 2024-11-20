package com.gc.baggoid.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gc.baggoid.models.*
import com.gc.baggoid.repo.GameStateRepositoryInterface
import com.gc.baggoid.views.FieldView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: GameStateRepositoryInterface
) : ViewModel(), FieldView.BagListener {

    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // LiveData to hold the game state
    private val _gameState: MutableLiveData<GameState> = MutableLiveData()
    val gameState: LiveData<GameState> = _gameState

    // LiveData to hold the team to place a bag in the current turn
    private val _currentTeam = MutableLiveData(Team.RED)
    val currentTeam: LiveData<Team> get() = _currentTeam

    // LiveData to hold the game rule
    private val _rulesMode = MutableLiveData<RulesMode>()
    val rulesMode: LiveData<RulesMode> get() = _rulesMode

    // Initialize the game state by loading it from the database or creating a new one
    init {
        val savedRuleMode = sharedPreferences.getString("rules_mode", RulesMode.SIMPLE.name)
        _rulesMode.value = RulesMode.valueOf(savedRuleMode ?: RulesMode.SIMPLE.name)

        viewModelScope.launch(Dispatchers.IO) {

            // Try to load the game state from the database
            val savedGameState = repository.getGameState()

            // If there's a saved game state, use it; otherwise, create a new one
            val gameStateToUse = savedGameState ?: GameState.newGame()

            // Post the game state to LiveData
            _gameState.postValue(gameStateToUse)

            // Set the current team based on the saved game state, or default to RED
            val savedCurrentTeam = savedGameState?.currentTeam ?: Team.RED
            _currentTeam.postValue(savedCurrentTeam)

        }
    }

    val clearBags: LiveData<Boolean> = Transformations.map(gameState) { gameState ->
        gameState.currentRound.isRoundNew
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
        val currentRulesMode = rulesMode.value ?: RulesMode.SIMPLE
        gameState.gameWinner(currentRulesMode)?.let { gameWinner ->
            GameOverState(
                gameWinner,
                gameState.redTeamTotalScore,
                gameState.blueTeamTotalScore
            )
        }
    }

    override fun onBagMoved(bagMovedEvent: BagMovedEvent) {
        _gameState.value = _gameState.value?.processEvent(bagMovedEvent)
        saveGameStateToDatabase(_gameState.value)
    }

    // Fetches the selected game rule from sharedPreferences
    fun loadRulesMode() {

        // Retrieve the saved rules mode from SharedPreferences
        val savedRuleMode = sharedPreferences.getString("rules_mode", RulesMode.SIMPLE.name)

        // Update the LiveData with the loaded rules mode, defaulting to SIMPLE if null
        _rulesMode.value = RulesMode.valueOf(savedRuleMode ?: RulesMode.SIMPLE.name)

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
        val currentRulesMode = rulesMode.value ?: RulesMode.SIMPLE
        _gameState.value = _gameState.value?.newRound(currentRulesMode)
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