package com.gc.baggoid.models

data class GameOverState(
    val gameWinner: Team,
    val redTeamTotalScore: Int,
    val blueTeamTotalScore: Int,
)
