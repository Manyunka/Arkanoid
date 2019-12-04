package com.tsu.arkanoid

import android.annotation.SuppressLint
import android.content.Context
import android.view.Display

@SuppressLint("ViewConstructor")
class Level3View(context: Context, gameDisplay: Display) : BreakoutEngine(context, gameDisplay) {
    override fun createBricks() {
        val brickWidth = ((screenX - 2 * pudding) / 9).toInt()
        val brickHeight = screenY / 18

        val levels = arrayOf(
            4, 1, 1, 1, 1, 1, 1, 1, 4,
            1, 1, 2, 2, 2, 2, 2, 1, 1,
            1, 2, 2, 3, 4, 3, 2, 2, 1,
            1, 2, 3, 3, 4, 3, 3, 2, 1,
            1, 2, 4, 4, 4, 4, 4, 4, 1,
            1, 2, 3, 3, 4, 3, 3, 2, 1,
            1, 2, 2, 3, 4, 3, 2, 2, 1,
            1, 1, 2, 2, 2, 2, 2, 1, 1,
            4, 1, 1, 1, 1, 1, 1, 1, 4
        )

        numBricks = 0
        for (column in 0..8) {
            for (row in 0..8) {
                bricks[numBricks] = Rose(
                    column * brickWidth.toFloat() + pudding,
                    row * brickHeight + 4 * pudding,
                    brickWidth, brickHeight, levels[numBricks], resources
                )
                numBricks++
            }
        }
    }

    init {
        enemyType = 3
        backgroundID = R.drawable.level3_background
    }
}