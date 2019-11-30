package com.tsu.arkanoid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory


abstract class Brick(x: Float, y: Float) {
    var isVisible: Boolean = false
    var x: Float
    var y: Float

    init {
        isVisible = true
        //val padding = 5

        this.x = x
        this.y = y
    }

    abstract fun getBrick(): Bitmap

    fun setInvisible() {
        isVisible = false
    }

    abstract fun decreaseLevel()

    fun getVisibility(): Boolean {
        return isVisible
    }
}