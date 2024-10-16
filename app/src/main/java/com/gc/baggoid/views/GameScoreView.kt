package com.gc.baggoid.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.gc.baggoid.databinding.PartialGameScoreBinding

class GameScoreView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val binding = PartialGameScoreBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        setRedTeamScore(0)
        setBlueTeamScore(0)
    }

    fun setRedTeamScore(redTeamScore: Int) {
        binding.redTeamScore.text = redTeamScore.toString()
    }

    fun setBlueTeamScore(blueTeamScore: Int) {
        binding.blueTeamScore.text = blueTeamScore.toString()
    }
}
