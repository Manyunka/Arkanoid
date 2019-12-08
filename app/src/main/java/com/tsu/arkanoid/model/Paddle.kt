package com.tsu.arkanoid.model

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tsu.arkanoid.R
import kotlin.math.abs


class Paddle(screenX: Int, screenY: Int, res: Resources) {
    private var paddle: Bitmap
    private var paddleNormal = BitmapFactory.decodeResource(res, R.drawable.paddle)
    private var paddleBonus = BitmapFactory.decodeResource(res, R.drawable.paddle_bonus)
    private var width: Int
    private var height: Int
    private var bonusTime = 0
    var x: Float
    var y: Float

    private val maxX: Int

    init {
        width = paddleNormal.width
        height = paddleNormal.height

        maxX = screenX - width

        x = (screenX / 2 - width / 2).toFloat()
        y = (screenY - 40 - height).toFloat()

        paddle = Bitmap.createScaledBitmap(paddleNormal, width, height, false)
    }

    fun changeWidth() {
        width = paddleBonus.width
        paddle = Bitmap.createScaledBitmap(paddleBonus, width, height, false)
        bonusTime = 500
    }

    fun getPaddle(): Bitmap {
        return paddle
    }

    fun update(speed: Float, timeFrame: Long) {
        x += if (abs(timeFrame * speed) > 5) (timeFrame * speed).toInt() else 0

        if (bonusTime > 0) {
            bonusTime--

            if (bonusTime < 1) {
                width = paddleNormal.width
                paddle = Bitmap.createScaledBitmap(paddleNormal, width, height, false)
            }
        }
    }

    fun reset(screenX: Int, screenY: Int) {
        x = (screenX / 2 - width / 2).toFloat()
        y = (screenY - 40 - height).toFloat()
    }

}