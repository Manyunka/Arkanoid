package com.tsu.arkanoid

import android.annotation.SuppressLint
import android.content.Context
import android.view.Display

@SuppressLint("ViewConstructor")
class Level1View(context: Context, gameDisplay: Display) : BreakoutEngine(context, gameDisplay) {

    override fun createBricks() {
        val brickWidth = ((screenX - 2 * pudding) / 9).toInt()
        val brickHeight = screenY / 18

        val levels = resources.getIntArray(R.array.Level1)

        numBricks = 0
        for (column in 0..8) {
            for (row in 0..5) {
                bricks[numBricks] = Star(
                    column * brickWidth.toFloat() + pudding,
                    row * brickHeight + 4 * pudding,
                    brickWidth, brickHeight, levels[numBricks], resources
                )
                numBricks++
            }
        }
    }

    init {
        level = 1
        enemyType = 1
        backgroundID = R.drawable.level1_background
    }
}