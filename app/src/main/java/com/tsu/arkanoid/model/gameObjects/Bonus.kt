package com.tsu.arkanoid.model.gameObjects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tsu.arkanoid.R
import java.util.*

class Bonus(screenX: Int, screenY: Int, pudding: Float, res: Resources) {
    private var bonus: Bitmap
    private var type: Int = Random().nextInt(4)
    private var expandBitmap = BitmapFactory.decodeResource(res, R.drawable.bonus_e)
    private var divideBitmap = BitmapFactory.decodeResource(res, R.drawable.bonus_d)
    private var laserBitmap = BitmapFactory.decodeResource(res, R.drawable.bonus_l)
    private var playerBitmap = BitmapFactory.decodeResource(res, R.drawable.bonus_p)

    private var width: Int
    private var height: Int
    private var yVel: Float = 200 * screenY / 1920f
    var x: Float
    var y: Float

    init {
        bonus = when (type) {
            EXPAND -> expandBitmap
            DIVIDE -> divideBitmap
            LASER -> laserBitmap
            PLAYER -> playerBitmap
            else -> playerBitmap
        }

        width = bonus.width
        height = bonus.height

        this.x = Random().nextInt(screenX - 2 * width).toFloat() + width
        this.y = 4 * pudding

        bonus = Bitmap.createScaledBitmap(bonus, width, height, false)
    }

    fun getTypeBonus(): Int {
        return type
    }

    fun getBonus(): Bitmap {
        return bonus
    }

    fun update(fps: Long) {
        y += yVel / fps
    }

    companion object {
        fun isAppeared(): Boolean {
            val answer = Random().nextInt(16)

            return answer == 0
        }

        const val EXPAND = 0
        const val DIVIDE = 1
        const val LASER = 2
        const val PLAYER = 3
    }
}