package com.tsu.arkanoid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.Display
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.abs
import android.view.MotionEvent
import android.media.SoundPool
import android.media.AudioAttributes


@SuppressLint("ViewConstructor")
class BreakoutEngine(context: Context, gameDisplay: Display) : SurfaceView(context), Runnable {

    private var gameThread: Thread? = null
    private var ourHolder: SurfaceHolder = holder

    @Volatile
    var playing = false
    private var paused = true

    private lateinit var canvas: Canvas
    private var paint: Paint = Paint()

    private var fps: Long = 0

    private var timeThisFrame: Long = 0

    private var screenX: Int
    private var screenY: Int

    private var paddle: Paddle

    private var ball: Ball

    private var bricks = arrayOfNulls<Brick>(100)
    private var numBricks = 0

    private var lives = 3

    private var soundPool: SoundPool
    private var popID: Int
    private var boundID: Int
    private var paddleID: Int

    private var orientationData: OrientationData

    init {
        val size = Point()
        gameDisplay.getSize(size)

        screenX = size.x
        screenY = size.y

        paddle = Paddle(screenX, screenY, resources)

        ball = Ball(screenX, screenY, resources)

        orientationData = OrientationData(context)
        orientationData.register()

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()

        popID = soundPool.load(context, R.raw.pop, 0)
        boundID = soundPool.load(context, R.raw.bound, 0)
        paddleID = soundPool.load(context, R.raw.paddle, 0)

        create()
    }

    private fun create() {
        paddle.reset(screenX, screenY)
        ball.reset(screenX, screenY)

        lives = 3

        val brickWidth = screenX / 10
        val brickHeight = screenY / 20

        numBricks = 0
        for (column in 0..9) {
            for (row in 0..5) {
                bricks[numBricks] = Brick(row, column, brickWidth, brickHeight, 2, resources)
                if (row == 5) bricks[numBricks] = Brick(row, column, brickWidth, brickHeight, 1, resources)
                if (column in 3..6 && row in 4..5) bricks[numBricks]!!.setInvisible()
                numBricks++
            }
        }
    }

    override fun run() {
        while (playing) {
            val startFrameTime = System.currentTimeMillis()
            if (!paused) {
                update()
            }
            draw()
            timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }

        }

    }

    private fun update() {
        ball.update(fps)
        //paddle.update(fps)

        val ballRect = RectF(ball.x, ball.y,
            ball.x + ball.getBall().width, ball.y + ball.getBall().height)
        val paddleRect = RectF(paddle.x, paddle.y,
            paddle.x + paddle.getPaddle().width,
            paddle.y + paddle.getPaddle().height)

       for (i in 0 until numBricks) {
            if (bricks[i]!!.getVisibility()) {
                val brickRect = RectF(bricks[i]!!.x, bricks[i]!!.y,
                    bricks[i]!!.x + bricks[i]!!.getBrick().width,
                    bricks[i]!!.y + bricks[i]!!.getBrick().height)

                if (RectF.intersects(brickRect, ballRect)) {
                    bricks[i]!!.decreaseLevel()
                    ball.reverseYVel()
                    soundPool.play(popID, 1f, 1f, 0, 0, 1f)
                }
            }
        }

        if (RectF.intersects(ballRect, paddleRect)) {
            ball.setRandomXVel()
            ball.reverseYVel()
            ball.clearObstacleY(paddle.y - 40)
            soundPool.play(paddleID, 1f, 1f, 0, 0, 1f)
        }
        if (ball.y > screenY) {
            ball.reverseYVel()
            ball.clearObstacleY(screenY - 2.0f)
            soundPool.play(boundID, 1f, 1f, 0, 0, 1f)

            lives--
            if (lives < 1) {
                paused = true
                create()
            }
        }

        if (ball.y < 0) {
            ball.reverseYVel()
            ball.clearObstacleY(2.0f)
            soundPool.play(boundID, 1f, 1f, 0, 0, 1f)
        }

        if (ball.x < 0) {
            ball.reverseXVelocity()
            ball.clearObstacleX(0.0f)
            soundPool.play(boundID, 1f, 1f, 0, 0, 1f)
        }

        if (ball.x > screenX - ballRect.width()) {

            ball.reverseXVelocity()
            ball.clearObstacleX(screenX - ballRect.width())
            soundPool.play(boundID, 1f, 1f, 0, 0, 1f)
        }

        if (orientationData.startOrientation != null) {
            val roll = orientationData.orientation[2] - orientationData.startOrientation!![2]

            val xSpeed = roll * screenX / 1080f

            paddle.x += if (abs(1000 / fps * xSpeed) > 5) (1000 / fps * xSpeed).toInt() else 0
        }

        if (paddle.x < 0) paddle.x = 0f
        if (paddle.x > screenX - paddleRect.width()) paddle.x = screenX - paddleRect.width()
    }

    private fun draw() {
        if (ourHolder.surface.isValid) {
            canvas = ourHolder.lockCanvas()
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            var background = BitmapFactory
                .decodeResource(resources, R.drawable.level1_background)

            background = Bitmap
                .createScaledBitmap(background, screenX, screenY, false)

            canvas.drawBitmap(background, 0f, 0f, paint)

            canvas.drawBitmap(ball.getBall(), ball.x, ball.y, paint)

            canvas.drawBitmap(paddle.getPaddle(), paddle.x, paddle.y, paint)

            for (i in 0 until numBricks) {
                if (bricks[i]!!.getVisibility()) {
                    canvas.drawBitmap(bricks[i]!!.getBrick(), bricks[i]!!.x, bricks[i]!!.y, paint)
                }
            }

            paint.color = Color.WHITE
            paint.textSize = 50f
            canvas.drawText("Lives: $lives", 10f, 50f, paint)

            ourHolder.unlockCanvasAndPost(canvas)
        }
    }

    fun pause(sensorManager: SensorManager) {
        playing = false
        try {
            gameThread!!.join()
            //sensorManager.unregisterListener(gyroListener)
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }

    }

    fun resume(sensorManager: SensorManager, sensor: Sensor) {
        playing = true
        gameThread = Thread(this)
        gameThread!!.start()
        //sensorManager.registerListener(gyroListener, sensor,
            //SensorManager.SENSOR_DELAY_NORMAL)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {

        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                paused = false
            }
        }
        return true
    }


    private var gyroListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, acc: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            paused = false
            if (event.values[2] < -0.3f) {
                paddle.setMovementState(paddle.RIGHT)
            } else if (event.values[2] > 0.3f) {
                paddle.setMovementState(paddle.LEFT)
            }
        }
    }

}