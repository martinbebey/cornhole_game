package com.gc.baggoid.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import com.gc.baggoid.R
import com.gc.baggoid.models.BagMovedEvent
import com.gc.baggoid.models.BagStatus
import com.gc.baggoid.models.Team

class FieldView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var bagListener: BagListener? = null

    private lateinit var boardBounds: Rect
    private lateinit var holeBounds: Rect

    // If we receive a touch event, there must not have been any bags in the way, so we should create
    // a new bag and add it to this view. We continue to catch the remainder of the touch event and
    // dispatch it to the new bag, until it can receive its own.

    private var newBagsAllowed = true
    private var bags: MutableList<BagView> = ArrayList()
    private var currentTeam: Team = Team.RED
        get() =
            if (field === Team.RED) {
                field = Team.BLUE
                Team.RED
            } else {
                field = Team.RED
                Team.BLUE
            }

    interface BagListener {
        fun onBagMoved(bagMovedEvent: BagMovedEvent)
    }

    init {
        val boardView: ImageView = makeBoard(context, R.drawable.board)
        boardView.z = 1f

        addView(makeBackground(context, R.drawable.bg_sunny))
        addView(boardView)

        post {
            boardBounds = getBoardBounds(boardView)
            holeBounds = getHole(boardBounds)
        }
    }

    /**
     * Returns the location of the bag - on the board, off the board, or in the hole
     */
    private fun getStatus(point: Point): BagStatus {
        if (holeBounds.contains(point.x, point.y)) {
            return BagStatus.IN_HOLE
        } else if (boardBounds.contains(point.x, point.y)) {
            return BagStatus.ON_BOARD
        }
        return BagStatus.OFF_BOARD
    }

    fun disallowNewBags() {
        newBagsAllowed = false
    }

    fun clearBags() {
        bags.forEach {
            removeView(it)
        }
        bags.clear()
        newBagsAllowed = true
        currentTeam = Team.RED
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!newBagsAllowed) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val newBag = BagView(context, currentTeam)
                newBag.centerAround(event.x, event.y)
                newBag.setOnTouchListener(bagTouchListener())
                addView(newBag)
                bags.add(newBag)
            }
        }
        // The latest bag added, still in drag mode
        return bags[bags.size - 1].dispatchTouchEvent(event)
    }

    private fun bagTouchListener(): OnTouchListener = object : OnTouchListener {
        private var origin: BagStatus? = null
        private var dX: Float = 0.toFloat()
        private var dY: Float = 0.toFloat()

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (origin == null) {
                        origin = BagStatus.IN_HAND
                    }
                    view.z = FLOATING_Z.toFloat()
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> view.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
                MotionEvent.ACTION_UP -> {
                    val bagView = view as BagView
                    val destination = getStatus(bagView.center)
                    view.setZ((if (destination === BagStatus.ON_BOARD) FLOATING_Z else 0).toFloat())
                    bagListener?.onBagMoved(BagMovedEvent(origin!!, destination, bagView.team))
                    origin = destination
                }
                else -> return false
            }
            return true
        }
    }
}

@Px private const val FLOATING_Z = 2

// The shadows are baked into the board asset; proportions of their respective dimensions
private const val LEFT_SHADOW = 0.0827
private const val RIGHT_SHADOW = 0.147
private const val TOP_SHADOW = 0.0339
private const val BOTTOM_SHADOW = 0.0823
private const val HOLE_TOP_OFFSET = 0.2085

private fun makeBackground(context: Context, @DrawableRes drawableRes: Int): ImageView {
    val bgView = ImageView(context)
    bgView.setImageResource(drawableRes)
    bgView.scaleType = ImageView.ScaleType.CENTER_CROP
    bgView.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
    return bgView
}


private fun makeBoard(context: Context, @DrawableRes drawableRes: Int): ImageView {
    val boardView = ImageView(context)
    boardView.setImageResource(drawableRes)
    boardView.scaleType = ImageView.ScaleType.CENTER_INSIDE

    // Layout with "large" top and bottom margins
    val boardLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
    @Px val verticalMargin = context.resources.getDimensionPixelSize(R.dimen.large)
    boardLayoutParams.setMargins(0, verticalMargin, 0, verticalMargin)
    boardLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
    boardView.layoutParams = boardLayoutParams

    // The image resource is slightly off-center because of the shadow, this centers it
    @Px val leftPadding = context.resources.getDimensionPixelSize(R.dimen.small)
    boardView.setPadding(leftPadding, 0, 0, 0)

    return boardView
}

private fun getBoardBounds(boardView: View): Rect {
    val rawWidth = boardView.width
    val rawHeight = boardView.height
    val leftShadow = (LEFT_SHADOW * rawWidth).toInt() + boardView.paddingLeft
    val topShadow = (TOP_SHADOW * rawHeight).toInt()
    val x = boardView.x.toInt() + leftShadow
    val y = boardView.y.toInt() + topShadow
    val width = rawWidth - leftShadow - (RIGHT_SHADOW * rawWidth).toInt()
    val height = rawHeight - topShadow - (BOTTOM_SHADOW * rawHeight).toInt()

    return Rect(x, y, x + width, y + height)
}

// Yeah, technically the hole is square
private fun getHole(board: Rect): Rect {
    val centerX = board.centerX()
    val centerY = board.top + (board.height() * HOLE_TOP_OFFSET).toInt()
    val radius = board.width() / 6
    return Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
}