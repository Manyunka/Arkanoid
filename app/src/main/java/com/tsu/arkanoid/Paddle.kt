package com.tsu.arkanoid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.abs


class Paddle(screenX: Int, screenY: Int, res: Resources) {
    private var paddle = BitmapFactory.decodeResource(res, R.drawable.paddle)
    private var width: Int
    private var height: Int
    var x: Float
    var y: Float

    private val maxX: Int

    init {
        width = paddle.width
        height = paddle.height

        maxX = screenX - width

        x = (screenX / 2).toFloat()
        y = (screenY - 40 - height).toFloat()

        paddle = Bitmap.createScaledBitmap(paddle, width, height, false)
    }

    fun getPaddle(): Bitmap {
        return paddle
    }

    fun update(speed: Float, timeFrame: Long) {
        x += if (abs(timeFrame * speed) > 5) (timeFrame * speed).toInt() else 0
    }

    fun reset(screenX: Int, screenY: Int) {
        x = (screenX / 2).toFloat()
        y = (screenY - 40 - height).toFloat()
    }

}