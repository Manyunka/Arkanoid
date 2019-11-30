package com.tsu.arkanoid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Bubble(x: Float, y: Float, width: Int, height: Int, private var level: Int, res: Resources) :
    Brick(x, y) {
    private var brick: Bitmap
    private val level1Bitmap = BitmapFactory.decodeResource(res, R.drawable.bubble_1)
    private val level2Bitmap = BitmapFactory.decodeResource(res, R.drawable.bubble_2)
    private val level3Bitmap = BitmapFactory.decodeResource(res, R.drawable.bubble_3)
    private val level4Bitmap = BitmapFactory.decodeResource(res, R.drawable.bubble_4)

    init {
        when (level) {
            1 -> brick = Bitmap.createScaledBitmap(level1Bitmap, width, height, false)
            2 -> brick = Bitmap.createScaledBitmap(level2Bitmap, width, height, false)
            3 -> brick = Bitmap.createScaledBitmap(level3Bitmap, width, height, false)
            4 -> brick = Bitmap.createScaledBitmap(level4Bitmap, width, height, false)
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
                4 -> brick =
                    Bitmap.createScaledBitmap(level3Bitmap, brick.width, brick.height, false)
            }
            level--
        } else isVisible = false
    }

    override fun getBrick(): Bitmap {
        return brick
    }
}