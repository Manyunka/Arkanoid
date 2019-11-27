package com.tsu.arkanoid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import java.util.*




class Ball(screenX: Int, screenY: Int, res: Resources) {
    private var ball = BitmapFactory.decodeResource(res, R.drawable.ball)
    private var xVel: Float
    private var yVel: Float
    private var width: Int
    private var height: Int
    var x: Float
    var y: Float

    init{
        xVel = 300 * screenX / 1080f
        yVel = -600 * screenY / 1920f

        width = ball.width
        height = ball.height

        x = (screenX / 2).toFloat()
        y = (screenY - 40 - 2 * height).toFloat()

        ball = Bitmap.createScaledBitmap(ball, width, height, false)
    }

    fun getBall(): Bitmap {
        return ball
    }

    fun update(fps: Long) {
        x += xVel / fps
        y += yVel / fps
        /*rect.left = rect.left + (xVel / fps);
        rect.top = rect.top + (yVel / fps);
        rect.right = rect.left + width;
        rect.bottom = rect.top - height;*/
    }

    fun reverseYVel() {
        yVel = -yVel
    }

    fun reverseXVelocity() {
        xVel = -xVel
    }

    fun setRandomXVel() {
        val generator = Random()
        val answer = generator.nextInt(2)

        if (answer == 0) {
            reverseXVelocity()
        }
    }

    fun clearObstacleY(y: Float) {
        this.y = y
        /*rect.bottom = y
        rect.top = y + height*/
    }

    fun clearObstacleX(x: Float) {
        this.x = x
        /*rect.left = x
        rect.right = x + width*/
    }

    fun reset(screenX: Int, screenY: Int) {
        x = (screenX / 2).toFloat()
        y = (screenY - 40 - 2 * height).toFloat()
        /*rect.left = (screenX / 2).toFloat()
        rect.top = (screenY - 40).toFloat()
        rect.right = screenX / 2 + width
        rect.bottom = screenY - 40 - height*/
    }
}