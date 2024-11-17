package com.gc.baggoid.utils

import androidx.room.TypeConverter
import com.gc.baggoid.models.RoundState
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
}
