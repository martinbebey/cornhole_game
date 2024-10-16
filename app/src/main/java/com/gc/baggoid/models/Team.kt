package com.gc.baggoid.models

import androidx.annotation.StringRes

import com.gc.baggoid.R

enum class Team {
    RED,
    BLUE;

    val displayNameRes: Int
        @StringRes get() = when (this) {
            RED -> R.string.red
            BLUE -> R.string.blue
        }
}
