package com.tsu.arkanoid.model

import android.graphics.Bitmap


abstract class Brick(x: Float, y: Float) {
    var isVisible: Boolean = false
    var x: Float
    var y: Float

    init {
        isVisible = true

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