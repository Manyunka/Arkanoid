package com.tsu.arkanoid

import android.annotation.SuppressLint
import android.content.Context
import android.view.Display

@SuppressLint("ViewConstructor")
class Level1View(context: Context, gameDisplay: Display) : BreakoutEngine(context, gameDisplay) {

    override fun createBricks() {
        val brickWidth = ((screenX - 3 * pudding) / 9).toInt()
        val brickHeight = screenY / 18

        val levels = arrayOf(
            0, 0, 0, 0, 1, 1,
            1, 2, 2, 2, 1, 1,
            1, 2, 3, 2, 1, 3,
            1, 2, 3, 2, 3, 0,
            3, 3, 3, 3, 0, 0,
            1, 2, 3, 2, 3, 0,
            1, 2, 3, 2, 1, 3,
            1, 2, 2, 2, 1, 1,
            0, 0, 0, 0, 1, 1
        )

        numBricks = 0
        for (column in 0..8) {
            for (row in 0..5) {
                bricks[numBricks] = Star(
                    column * brickWidth.toFloat(),
                    row * brickHeight + 4 * pudding,
                    brickWidth, brickHeight, levels[numBricks], resources
                )
                numBricks++
            }
        }
    }

    init {
        backgroundID = R.drawable.level1_background
    }
}