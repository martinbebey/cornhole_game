package com.gc.baggoid.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameState(
    val redTeamTotalScore: Int = 0,
    val blueTeamTotalScore: Int = 0,
    val currentRoundNumber: Int = 1,
    val currentRound: RoundState = RoundState(),
    @ColumnInfo(name = "current_team")
    var currentTeam: Team = Team.RED,

    @PrimaryKey
    val id: Int  = 99
) {
    companion object {
        fun newGame() = GameState()
    }

//    var ruleMode: RulesMode = RulesMode.SIMPLE
//
//    fun setRules(rules: RulesMode){
//        ruleMode = rules
//    }

    // Game winner based on rulesMode (rulesMode will now be passed as an argument to the method)
    fun gameWinner(rulesMode: RulesMode): Team? {
        return when (rulesMode) {
            RulesMode.SIMPLE -> simpleWinCondition()
            RulesMode.EXACT -> exact21WinCondition()
        }
    }

    private fun simpleWinCondition(): Team? {
        // Use the existing rule: first team to 21 points with a 2-point lead wins
        return if (redTeamTotalScore >= SCORE_TO_WIN && redTeamTotalScore - blueTeamTotalScore >= WIN_BY)
            Team.RED
        else if (blueTeamTotalScore >= SCORE_TO_WIN && blueTeamTotalScore - redTeamTotalScore >= WIN_BY)
            Team.BLUE
        else
            null
    }

    private fun exact21WinCondition(): Team? {
        // New rule: a team must reach exactly 21 points to win
        return when {
            redTeamTotalScore == 21 -> Team.RED
            blueTeamTotalScore == 21 -> Team.BLUE
            else -> null
        }
    }

    fun newRound(rulesMode: RulesMode): GameState {
        return copy(
            redTeamTotalScore = calculateTotalScore(Team.RED, rulesMode),
            blueTeamTotalScore = calculateTotalScore(Team.BLUE, rulesMode),
            currentRoundNumber = currentRoundNumber + 1,
            currentRound = RoundState(),
        )
    }

    fun processEvent(event: BagMovedEvent): GameState {
        val intermediate = processOrigin(event.team, event.origin)
        return intermediate.processResult(event.team, event.result)
    }

    private fun processOrigin(team: Team, origin: BagStatus): GameState {
        val round = when (origin) {
            BagStatus.IN_HAND -> currentRound.decrementBagsRemaining(team)
            else -> currentRound.modifyScore(team, -origin.points)
        }

        if(team == Team.RED) currentTeam = Team.BLUE else currentTeam = Team.RED

        return copy(currentRound = round)
    }

    private fun processResult(team: Team, result: BagStatus): GameState {
        val round = when (result) {
            BagStatus.IN_HAND -> throw IllegalArgumentException("You can't pick the bags up until the round is over.")
            else -> currentRound.modifyScore(team, result.points)
        }
        return copy(currentRound = round)
    }

    private fun calculateTotalScore(team: Team, rulesMode: RulesMode): Int {
        var teamTotalScore = when (team) {
            Team.RED -> redTeamTotalScore
            Team.BLUE -> blueTeamTotalScore
        }
        if (currentRound.roundWinner == team) {
            teamTotalScore += currentRound.roundDelta
        }

        return when (rulesMode) {
            RulesMode.SIMPLE -> teamTotalScore
            RulesMode.EXACT -> if (teamTotalScore > 21) 11 else teamTotalScore
        }
    }
}
