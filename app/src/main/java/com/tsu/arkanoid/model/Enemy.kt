package com.tsu.arkanoid.model

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tsu.arkanoid.R
import java.util.*

class Enemy(type: Int, screenX: Int, screenY: Int, pudding: Float, res: Resources) {
    private var enemy: Bitmap
    private var enemy1 = BitmapFactory.decodeResource(res, R.drawable.enemy_1)
    private var enemy2 = BitmapFactory.decodeResource(res, R.drawable.enemy_2)
    private var enemy3 = BitmapFactory.decodeResource(res, R.drawable.enemy_3)

    private var width: Int
    private var height: Int
    private var yVel: Float
    var x: Float
    var y: Float

    init {
        enemy = when (type) {
            1 -> enemy1
            2 -> enemy2
            3 -> enemy3
            else -> enemy1
        }

        width = enemy.width
        height = enemy.height

        this.x = Random().nextInt(screenX - 2 * width).toFloat() + width
        this.y = 4 * pudding

        yVel = 200 * screenY / 1920f

        enemy = Bitmap.createScaledBitmap(enemy, width, height, false)
    }

    fun getEnemy(): Bitmap {
        return enemy
    }

    fun update(fps: Long) {
        y += yVel / fps
    }

    companion object {
        fun isAppeared(type: Int): Boolean {
            val bound = when (type) {
                1 -> 15
                2 -> 10
                3 -> 5
                else -> 15
            }
            val answer = Random().nextInt(bound)

            return answer == 0
        }
    }
}