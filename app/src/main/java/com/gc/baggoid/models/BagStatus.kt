package com.gc.baggoid.models

enum class BagStatus(val points: Int) {
    IN_HAND(0),
    ON_BOARD(1),
    IN_HOLE(3),
    OFF_BOARD(0)
}
