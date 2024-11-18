package com.gc.baggoid.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gc.baggoid.viewmodel.GameViewModel
import com.gc.baggoid.R
import com.gc.baggoid.databinding.ActivityGameBinding
import com.gc.baggoid.models.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : AppCompatActivity(R.layout.activity_game) {

    private val binding: ActivityGameBinding by lazy { ActivityGameBinding.inflate(layoutInflater) }
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        setupStateObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scoring, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_item_rules -> {
            showRules()
            true
        }
        R.id.menu_item_restart -> {
            viewModel.startNewGame()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        binding.fieldView.bagListener = null
        super.onDestroy()
    }

    private fun setupViews() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        binding.redRoundScoreView.setBagsPerRound(BAGS_PER_ROUND)
        binding.blueRoundScoreView.setBagsPerRound(BAGS_PER_ROUND)
        binding.fieldView.bagListener = viewModel

        viewModel.currentTeam.observe(this) { currentTeam ->
            binding.fieldView.setCurrentTeam(currentTeam)
        }
    }

    private fun setupStateObservers() {
        viewModel.gameState.observe(this) { gameState ->
            updateScoreboard(gameState)
        }

        viewModel.clearBags.observe(this) { clearBags ->
            if (clearBags) binding.fieldView.clearBags()
        }

        viewModel.roundOverState.observe(this) { roundOverState ->
            roundOverState?.let {
                showRoundOverDialog(it.roundWinner, it.roundNumber, it.victoryMargin)
            }
        }

        viewModel.gameOverState.observe(this) { gameOverState ->
            gameOverState?.let {
                showGameOverDialog(it.gameWinner, it.redTeamTotalScore, it.blueTeamTotalScore)
            }
        }
    }

    private fun updateScoreboard(gameState: GameState) {
        binding.gameScoreView.setRedTeamScore(gameState.redTeamTotalScore)
        binding.gameScoreView.setBlueTeamScore(gameState.blueTeamTotalScore)
        binding.redRoundScoreView.setRoundScore(gameState.currentRound.redTeamRoundScore)
        binding.blueRoundScoreView.setRoundScore(gameState.currentRound.blueTeamRoundScore)
        binding.redRoundScoreView.setBagsRemaining(gameState.currentRound.redTeamBagsRemaining)
        binding.blueRoundScoreView.setBagsRemaining(gameState.currentRound.blueTeamBagsRemaining)
        binding.roundIndicator.text = getString(R.string.round, gameState.currentRoundNumber)
    }

    private fun showRoundOverDialog(roundWinner: Team?, roundNumber: Int, victoryMargin: Int) {
        binding.fieldView.disallowNewBags()
        val title: String
        val message: String
        if (roundWinner != null) {
            val teamName = getString(roundWinner.displayNameRes)
            title = getString(R.string.dialog_title_round_win, teamName, roundNumber)
            message = resources.getQuantityString(
                R.plurals.dialog_msg_round_win,
                victoryMargin,
                victoryMargin,
            )
        } else {
            title = getString(R.string.dialog_title_round_tie, roundNumber)
            message = getString(R.string.dialog_msg_round_tie)
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.start_next_round) { _, _ -> viewModel.startNewRound() }
            .setCancelable(false)
            .show()
    }

    private fun showGameOverDialog(winner: Team, redScore: Int, blueScore: Int) {
        val teamName = getString(winner.displayNameRes)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title_game_over, teamName))
            .setPositiveButton(R.string.start_new_game) { _, _ -> viewModel.startNewGame() }
            .setMessage(getString(R.string.final_score, redScore, blueScore))
            .setCancelable(false)
            .show()
    }

    private fun showRules() {
        startActivity(Intent(this, RulesActivity::class.java))
    }
}
