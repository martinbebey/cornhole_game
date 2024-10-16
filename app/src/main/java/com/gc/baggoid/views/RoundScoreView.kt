package com.gc.baggoid.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import com.gc.baggoid.R
import com.gc.baggoid.databinding.PartialRoundScoreBinding

class RoundScoreView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val binding = PartialRoundScoreBinding.inflate(LayoutInflater.from(context), this)

    @DrawableRes
    var bgRes: Int = 0

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundScoreView)
        bgRes = attributes.getResourceId(R.styleable.RoundScoreView_indicatorBackground, -1)
        attributes.recycle()
    }

    private fun bagLayoutParams(): LayoutParams {
        @Px val sideLength = resources.getDimensionPixelSize(R.dimen.xsmall)
        @Px val horizontalMargin = resources.getDimensionPixelSize(R.dimen.xxxsmall)
        return LayoutParams(sideLength, sideLength).apply {
            setMargins(horizontalMargin, 0, horizontalMargin, 0)
        }
    }

    fun setBagsPerRound(bagsPerRound: Int) {
        binding.remainingBagsContainer.removeAllViews()
        for (i in 0 until bagsPerRound) {
            val bag = View(context)
            bag.background = ContextCompat.getDrawable(context, bgRes)
            bag.layoutParams = bagLayoutParams()
            binding.remainingBagsContainer.addView(bag)
        }
    }

    fun setBagsRemaining(bagsRemaining: Int) {
        binding.remainingBagsContainer.forEachIndexed { index, view ->
            view.isSelected = index < bagsRemaining
        }
    }

    fun setRoundScore(roundScore: Int) {
        binding.scoreText.text = roundScore.toString()
    }
}
