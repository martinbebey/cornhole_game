package com.gc.baggoid.models
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "game_state")
data class GameState(
    val redTeamTotalScore: Int = 0,
    val blueTeamTotalScore: Int = 0,
    val currentRoundNumber: Int = 1,
    val currentRound: RoundState = RoundState(),

//    @PrimaryKey
    val id: Int  = 99
) {
    companion object {
        fun newGame() = GameState()
    }

    val gameWinner: Team?
        get() {
            return if (redTeamTotalScore >= SCORE_TO_WIN && redTeamTotalScore - blueTeamTotalScore >= WIN_BY)
                Team.RED
            else if (blueTeamTotalScore >= SCORE_TO_WIN && blueTeamTotalScore - redTeamTotalScore >= WIN_BY)
                Team.BLUE
            else
                return null
        }

    fun newRound(): GameState {
        return copy(
            redTeamTotalScore = calculateTotalScore(Team.RED),
            blueTeamTotalScore = calculateTotalScore(Team.BLUE),
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
        return copy(currentRound = round)
    }

    private fun processResult(team: Team, result: BagStatus): GameState {
        val round = when (result) {
            BagStatus.IN_HAND -> throw IllegalArgumentException("You can't pick the bags up until the round is over.")
            else -> currentRound.modifyScore(team, result.points)
        }
        return copy(currentRound = round)
    }

    private fun calculateTotalScore(team: Team): Int {
        var teamTotalScore = when (team) {
            Team.RED -> redTeamTotalScore
            Team.BLUE -> blueTeamTotalScore
        }
        if (currentRound.roundWinner == team) {
            teamTotalScore += currentRound.roundDelta
        }
        return teamTotalScore
    }
}
