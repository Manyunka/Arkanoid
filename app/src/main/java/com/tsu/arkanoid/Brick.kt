package com.tsu.arkanoid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory


class Brick(x: Float, y: Float, width: Int, height: Int, private var level: Int, res: Resources) {
    private val brick1 = BitmapFactory.decodeResource(res, R.drawable.bubble_blue)
    private val brick2 = BitmapFactory.decodeResource(res, R.drawable.bubble_green)
    private var brick: Bitmap
    private var isVisible: Boolean = false
    var x: Float
    var y: Float

    init {
        isVisible = true
        val padding = 5

        brick = if (level == 2) Bitmap.createScaledBitmap(brick2, width, height, false)
        else Bitmap.createScaledBitmap(brick1, width, height, false)

        this.x = x
        this.y = y
        //x = (column * width + padding).toFloat()
        //y = (row * height + padding).toFloat()

        /*rect = RectF(
            (column * width + padding).toFloat(),
            (row * height + padding).toFloat(),
            (column * width + width - padding).toFloat(),
            (row * height + height - padding).toFloat()
        )*/
    }

    fun getBrick(): Bitmap {
        return brick
    }

    fun setInvisible() {
        isVisible = false
    }

    fun decreaseLevel() {
        if (level > 1) {
            level--
            brick = Bitmap.createScaledBitmap(brick1, brick.width, brick.height, false)
        } else isVisible = false
    }

    fun getVisibility(): Boolean {
        return isVisible
    }
}