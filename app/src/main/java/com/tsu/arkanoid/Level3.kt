package com.tsu.arkanoid

import android.annotation.SuppressLint
import android.content.Context
import android.view.Display
import com.tsu.arkanoid.model.gameObjects.bricks.Rose

@SuppressLint("ViewConstructor")
class Level3(context: Context, gameDisplay: Display) : BreakoutEngine(context, gameDisplay) {
    override fun createBricks() {
        val brickWidth = ((screenX - 2 * pudding) / 9).toInt()
        val brickHeight = screenY / 18

        val levels = resources.getIntArray(R.array.Level3)

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
        level = 3
        backgroundID = R.drawable.level3_background
    }
}