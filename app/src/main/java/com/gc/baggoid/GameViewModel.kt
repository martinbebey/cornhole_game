package com.gc.baggoid

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

//class GameViewModel (
//    private val repository: GameStateRepositoryInterface = Graph.gameStateRepository
//): ViewModel(), FieldView.BagListener {
//
//    private val _gameState: MutableLiveData<GameState> = MutableLiveData(GameState.newGame())
//    val gameState: LiveData<GameState> = _gameState
//
////    lateinit var savedGameState: GameState
////
////    init{
////        viewModelScope.launch(Dispatchers.IO) {
////            savedGameState = repository.getGameState()
////        }
////
////        _gameState.value = savedGameState
////    }
//
//    val clearBags: LiveData<Boolean> = Transformations.map(gameState) { gameState ->
//        gameState.currentRound.isRoundNew
//    }
//
//    val roundOverState: LiveData<RoundOverState?> = Transformations.map(gameState) { gameState ->
//        if (gameState.currentRound.isRoundOver) {
//            RoundOverState(
//                gameState.currentRoundNumber,
//                gameState.currentRound.roundWinner,
//                gameState.currentRound.roundDelta,
//            )
//        } else null
//    }
//
//    val gameOverState: LiveData<GameOverState?> = Transformations.map(gameState) { gameState ->
//        gameState.gameWinner?.let { gameWinner ->
//            GameOverState(
//                gameWinner,
//                gameState.redTeamTotalScore,
//                gameState.blueTeamTotalScore,
//            )
//        }
//    }
//
//    override fun onBagMoved(bagMovedEvent: BagMovedEvent) {
//        _gameState.value = _gameState.value?.processEvent(bagMovedEvent)
//    }
//
//    fun startNewGame() {
//        _gameState.value = GameState.newGame()
//    }
//
//    fun startNewRound() {
//        _gameState.value = _gameState.value?.newRound()
//    }
//}

class GameViewModel @Inject constructor(
    private val repository: GameStateRepositoryInterface = Graph.gameStateRepository
) : ViewModel(), FieldView.BagListener {

    // LiveData to hold the game state
    private val _gameState: MutableLiveData<GameState> = MutableLiveData()
    val gameState: LiveData<GameState> = _gameState

    // Initialize the game state by loading it from the database or creating a new one
    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Try to load the game state from the database
            val savedGameState = repository.getGameState()

            // If there's a saved game state, use it; otherwise, create a new one
            _gameState.postValue(savedGameState ?: GameState.newGame())
        }
    }

    // Derived LiveData for UI updates
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
        // Optionally, save the game state to the database here
        saveGameStateToDatabase(_gameState.value)
    }

    // Start a new game, resetting the state
    fun startNewGame() {
        val newGameState = GameState.newGame()
        _gameState.value = newGameState
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

