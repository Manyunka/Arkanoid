package com.tsu.arkanoid.model.gameObjects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tsu.arkanoid.R
import java.lang.Math.PI
import java.lang.Math.cos

import java.util.*
import kotlin.math.atan


class Ball(screenX: Int, screenY: Int, x: Float, angle: Int, res: Resources) {
    private var ball = BitmapFactory.decodeResource(res, R.drawable.ball)
    private var xVel: Float
    private var yVel: Float
    private var width: Int
    private var height: Int
    var x: Float
    var y: Float

    init{
        xVel = (kotlin.math.cos(angle * (PI / 180)) * 300 * screenX / 1080f).toFloat()
        yVel = -(kotlin.math.sin(angle * (PI / 180)) * 300 * screenY / 1920f).toFloat()

        width = ball.width
        height = ball.height

        this.x = x - width / 2
        y = (screenY - 40 - height).toFloat()

        ball = Bitmap.createScaledBitmap(ball, width, height, false)
    }

    fun getBall(): Bitmap {
        return ball
    }

    fun update(fps: Long) {
        x += xVel / fps
        y += yVel / fps
    }

    fun reverseYVel() {
        yVel = -yVel
    }

    fun reverseXVel() {
        xVel = -xVel
    }

    fun setRandomXVel() {
        val generator = Random()
        val answer = generator.nextInt(2)

        if (answer == 0) {
            reverseXVel()
        }
    }

    fun clearObstacleY(y: Float) {
        this.y = y
    }

    fun clearObstacleX(x: Float) {
        this.x = x
    }

    fun reset(screenX: Int, screenY: Int) {
        xVel = (kotlin.math.cos(45 * (PI / 180)) * 300 * screenX / 1080f).toFloat()
        yVel = -(kotlin.math.sin(45 * (PI / 180)) * 300 * screenY / 1920f).toFloat()
        x = (screenX / 2 - width / 2).toFloat()
        y = (screenY - 40 - height).toFloat()
    }

    companion object {
        fun generateAngle(): Int {
            return Random().nextInt(121) + 30
        }
    }
}