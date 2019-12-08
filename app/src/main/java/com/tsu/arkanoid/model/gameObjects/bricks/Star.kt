package com.tsu.arkanoid.model.gameObjects.bricks

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tsu.arkanoid.R

class Star(x: Float, y: Float, width: Int, height: Int, level: Int, res: Resources) :
    Brick(x, y, level) {
    private var brick: Bitmap
    private val level1Bitmap = BitmapFactory.decodeResource(res, R.drawable.star_1)
    private val level2Bitmap = BitmapFactory.decodeResource(res, R.drawable.star_2)
    private val level3Bitmap = BitmapFactory.decodeResource(res, R.drawable.star_3)

    init {
        when (level) {
            1 -> brick = Bitmap.createScaledBitmap(level1Bitmap, width, height, false)
            2 -> brick = Bitmap.createScaledBitmap(level2Bitmap, width, height, false)
            3 -> brick = Bitmap.createScaledBitmap(level3Bitmap, width, height, false)
            else -> {
                brick = Bitmap.createScaledBitmap(level1Bitmap, width, height, false)
                isVisible = false
            }
        }
    }

    override fun decreaseLevel() {
        if (level > 1) {
            when (level) {
                2 -> brick =
                    Bitmap.createScaledBitmap(level1Bitmap, brick.width, brick.height, false)
                3 -> brick =
                    Bitmap.createScaledBitmap(level2Bitmap, brick.width, brick.height, false)
            }
            level--
        } else isVisible = false
    }

    override fun getBrick(): Bitmap {
        return brick
    }
}