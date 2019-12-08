package com.tsu.arkanoid.model

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.tsu.arkanoid.R


class Bullet(screenY: Int, var x: Float, res: Resources) {
    private var bullet  = BitmapFactory.decodeResource(res, R.drawable.bullet)

    private var width: Int
    private var height: Int
    var y: Float
    private var yVel: Float = -400 * screenY / 1920f

    init {
        width = bullet.width
        height = bullet.height

        y = (screenY - 40 - height).toFloat()

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false)
    }

    fun getBullet(): Bitmap {
        return bullet
    }

    fun update(fps: Long) {
        y += yVel / fps
    }
}