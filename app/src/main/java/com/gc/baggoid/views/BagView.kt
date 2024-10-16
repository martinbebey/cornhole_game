package com.gc.baggoid.views

import android.content.Context
import android.graphics.Point
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import com.gc.baggoid.R
import com.gc.baggoid.models.Team

class BagView @JvmOverloads constructor(context: Context, val team: Team = Team.RED) : AppCompatImageView(context) {

    init {
        setImageResource(when (team) {
            Team.RED -> R.drawable.bag_red
            Team.BLUE -> R.drawable.bag_blue
        })
        @Px val bagSize = resources.getDimensionPixelSize(R.dimen.large)
        layoutParams = ViewGroup.LayoutParams(bagSize, bagSize)
        rotation = Math.random().toFloat() * 180
    }

    val center: Point
        get() = (layoutParams.width / 2).let { halfWidth ->
            Point(x.toInt() + halfWidth, y.toInt() + halfWidth)
        }

    fun centerAround(x: Float, y: Float) {
        setX(x - layoutParams.width / 2)
        setY(y - layoutParams.width / 2)
    }
}
