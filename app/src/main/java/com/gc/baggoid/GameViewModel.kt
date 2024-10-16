package com.gc.baggoid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gc.baggoid.models.*
import com.gc.baggoid.views.FieldView

class GameViewModel : ViewModel(), FieldView.BagListener {

    private val _gameState: MutableLiveData<GameState> = MutableLiveData(GameState.newGame())
    val gameState: LiveData<GameState> = _gameState

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

    override fun onBagMoved(bagMovedEvent: BagMovedEvent) {
        _gameState.value = _gameState.value?.processEvent(bagMovedEvent)
    }

    fun startNewGame() {
        _gameState.value = GameState.newGame()
    }

    fun startNewRound() {
        _gameState.value = _gameState.value?.newRound()
    }
}
