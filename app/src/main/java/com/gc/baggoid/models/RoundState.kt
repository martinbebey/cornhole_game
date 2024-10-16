package com.gc.baggoid.models

import kotlin.math.abs

data class RoundState(
    val redTeamRoundScore: Int = 0,
    val blueTeamRoundScore: Int = 0,
    val redTeamBagsRemaining: Int = BAGS_PER_ROUND,
    val blueTeamBagsRemaining: Int = BAGS_PER_ROUND,
) {
    val roundWinner: Team?
        get() {
            if (!isRoundOver) return null
            return when {
                redTeamRoundScore > blueTeamRoundScore -> Team.RED
                redTeamRoundScore < blueTeamRoundScore -> Team.BLUE
                else -> null
            }
        }

    val roundDelta: Int
        get() = abs(redTeamRoundScore - blueTeamRoundScore)

    val isRoundNew: Boolean
        get() = redTeamBagsRemaining == BAGS_PER_ROUND && blueTeamBagsRemaining == BAGS_PER_ROUND

    val isRoundOver: Boolean
        get() = redTeamBagsRemaining == 0 && blueTeamBagsRemaining == 0

    fun decrementBagsRemaining(team: Team): RoundState {
        return when (team) {
            Team.RED -> copy(redTeamBagsRemaining = redTeamBagsRemaining - 1)
            Team.BLUE -> copy(blueTeamBagsRemaining = blueTeamBagsRemaining - 1)
        }
    }

    fun modifyScore(team: Team, value: Int): RoundState {
        return when (team) {
            Team.RED -> copy(redTeamRoundScore = redTeamRoundScore + value)
            Team.BLUE -> copy(blueTeamRoundScore = blueTeamRoundScore + value)
        }
    }
}
