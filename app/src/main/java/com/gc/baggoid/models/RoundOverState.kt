package com.gc.baggoid.models

data class RoundOverState(
    val roundNumber: Int,
    val roundWinner: Team?,
    val victoryMargin: Int,
)
