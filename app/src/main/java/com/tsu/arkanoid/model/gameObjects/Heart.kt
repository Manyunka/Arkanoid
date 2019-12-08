package com.tsu.arkanoid.model.gameObjects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tsu.arkanoid.R

class Heart(var x: Float, var y: Float, width: Int, height: Int, type: Int, res: Resources) {
    private var heart: Bitmap
    private val heartLive = BitmapFactory.decodeResource(res, R.drawable.heart)
    private val heartLost = BitmapFactory.decodeResource(res, R.drawable.heart_lost)

    init {
        heart = when (type) {
            1 -> Bitmap.createScaledBitmap(heartLive, heartLive.width, heartLive.height, false)
            else -> {
                Bitmap.createScaledBitmap(heartLost, heartLost.width, heartLost.height, false)
            }
        }
    }

    fun getHeart(): Bitmap {
        return heart
    }

    fun changeType(type: Int) {
        heart = when (type) {
            1 -> Bitmap.createScaledBitmap(heartLive, heart.width, heart.height, false)
            else -> {
                Bitmap.createScaledBitmap(heartLost, heart.width, heart.height, false)
            }
        }
    }
}