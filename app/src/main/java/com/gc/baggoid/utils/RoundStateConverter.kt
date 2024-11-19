package com.gc.baggoid.utils

import androidx.room.TypeConverter
import com.gc.baggoid.models.RoundState
import com.gc.baggoid.models.Team
import com.google.gson.Gson

class RoundStateConverter {

    private val gson = Gson()

    // Convert RoundState object to JSON string
    @TypeConverter
    fun fromRoundState(value: RoundState): String {
        return gson.toJson(value)
    }

    // Convert JSON string back to RoundState object
    @TypeConverter
    fun toRoundState(value: String): RoundState {
        return gson.fromJson(value, RoundState::class.java)
    }

    // Convert Team object to JSON int
    @TypeConverter
    fun fromTeam(team: Team): Int {
        return team.ordinal  // Store the enum ordinal (0 = RED, 1 = BLUE)
    }

    // Convert JSON int back to Team object
    @TypeConverter
    fun toTeam(ordinal: Int): Team {
        return Team.values()[ordinal]
    }
}
