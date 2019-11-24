package com.tsu.arkanoid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF



class Paddle(screenX: Int, screenY: Int, res: Resources) {
    private var paddle = BitmapFactory.decodeResource(res, R.drawable.paddle)
    private var width: Int
    private var height: Int
    var x: Float
    var y: Float

    private var paddleSpeed: Float

    private val STOP = 0
    val LEFT = 1
    val RIGHT = 2
    val maxX: Int

    private var paddleMoving = STOP
    init {
        width = (paddle.width * screenX / 1080f).toInt()
        height = (paddle.height * screenY / 1920f).toInt()

        maxX = screenX - width

        x = (screenX / 2).toFloat()
        y = (screenY - 40 - height).toFloat()

        paddle = Bitmap.createScaledBitmap(paddle, width, height, false)

        paddleSpeed = 350f
    }

    fun getPaddle(): Bitmap {
        return paddle
    }

    fun setMovementState(state: Int) {
        paddleMoving = state
    }

    fun update(fps: Long) {
       if (paddleMoving == LEFT && x > 0) {
            x -= paddleSpeed / fps
        }

        if (paddleMoving == RIGHT && x < maxX) {
            x += paddleSpeed / fps
        }

        //rect.left = x
        //rect.right = x + length
    }

    fun reset(screenX: Int, screenY: Int) {
        x = (screenX / 2).toFloat()
        /*rect.left = (screenX / 2).toFloat()
        rect.top = (screenY - 20).toFloat()
        rect.right = screenX / 2 + length
        rect.bottom = screenY.toFloat() - 20f - height*/
    }

}