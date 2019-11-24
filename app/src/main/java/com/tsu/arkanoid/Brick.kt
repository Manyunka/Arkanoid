package com.tsu.arkanoid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF



class Brick(row: Int, column: Int, width: Int, height: Int, res: Resources) {
    private var brick: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bubble_blue)
    private var isVisible: Boolean = false
    var x: Float
    var y: Float

    init {

        isVisible = true
        val padding = 2

        brick = Bitmap.createScaledBitmap(brick, width, height, false)

        x = (column * width + padding).toFloat()
        y = (row * height + padding).toFloat()

        /*rect = RectF(
            (column * width + padding).toFloat(),
            (row * height + padding).toFloat(),
            (column * width + width - padding).toFloat(),
            (row * height + height - padding).toFloat()
        )*/
    }

    fun getBrick(): Bitmap {
        return brick
    }

    fun setInvisible() {
        isVisible = false
        /*if(!isVisible){
            isVisible = true;
            return "green";
        }
        else{
            isVisible  = false;
            return "red";
        }*/
    }

    fun getVisibility(): Boolean {
        return isVisible
    }
}