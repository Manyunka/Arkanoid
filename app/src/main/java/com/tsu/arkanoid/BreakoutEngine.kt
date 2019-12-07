package com.tsu.arkanoid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.Display
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.MotionEvent
import android.media.SoundPool
import android.media.AudioAttributes
import androidx.core.content.ContextCompat
import com.tsu.arkanoid.model.*

@SuppressLint("ViewConstructor")
abstract class BreakoutEngine(context: Context, gameDisplay: Display) : SurfaceView(context),
    Runnable {

    private var gameThread: Thread? = null
    private var ourHolder: SurfaceHolder = holder

    @Volatile
    var playing = false
    private var paused = true
    private var gameOver = false

    var level = -1

    private lateinit var canvas: Canvas
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var fps: Long = 0
    private var timeThisFrame: Long = 0

    var screenX: Int
    var screenY: Int
    var pudding = 25.0f

    var backgroundID = -1

    private var paddle: Paddle
    private var ball: Ball
    private var enemies = arrayOfNulls<Enemy>(100)
    var bricks = arrayOfNulls<Brick>(100)

    var numBricks = 0

    private var lives = 3
    private var score = 0

    private var soundPool: SoundPool
    private var popID: Int
    private var boundID: Int
    private var paddleID: Int
    private var enemyID: Int
    private var lostID: Int

    private var orientationData: OrientationData

    init {
        val size = Point()
        gameDisplay.getSize(size)

        screenX = size.x
        screenY = size.y

        paddle = Paddle(screenX, screenY, resources)
        ball = Ball(screenX, screenY, resources)

        orientationData = OrientationData(context)

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
        enemyID = soundPool.load(context, R.raw.enemy, 0)
        lostID = soundPool.load(context, R.raw.lost, 0)

        create()
    }

    private fun create() {
        paddle.reset(screenX, screenY)
        ball.reset(screenX, screenY - paddle.getPaddle().height)
        pudding = ball.getBall().width.toFloat()

        orientationData.register()

        lives = 3
        score = 0

        createBricks()
    }

    override fun run() {
        while (playing) {
            val startFrameTime = System.currentTimeMillis()
            if (!paused && !gameOver) {
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

        updatePaddle()

        val ballRect = RectF(
            ball.x, ball.y,
            ball.x + ball.getBall().width, ball.y + ball.getBall().height
        )
        val paddleRect = RectF(
            paddle.x, paddle.y,
            paddle.x + paddle.getPaddle().width,
            paddle.y + paddle.getPaddle().height
        )

        for (i in 0 until numBricks) {
            if (enemies[i] != null) {
                enemies[i]!!.update(fps)

                val enemyRect = RectF(
                    enemies[i]!!.x, enemies[i]!!.y,
                    enemies[i]!!.x + enemies[i]!!.getEnemy().width,
                    enemies[i]!!.y + enemies[i]!!.getEnemy().height
                )
                if (RectF.intersects(enemyRect, ballRect) || RectF.intersects(
                        enemyRect,
                        paddleRect
                    )
                ) {
                    enemies[i] = null
                    playSound(enemyID)
                    score += 10
                } else if (enemies[i]!!.y > screenY) {
                    lives--
                    enemies[i] = null
                    playSound(lostID)
                    if (lives < 1) return
                }

            }
        }

        for (i in 0 until numBricks) {
            if (bricks[i]!!.getVisibility()) {
                val brickRect = RectF(
                    bricks[i]!!.x, bricks[i]!!.y,
                    bricks[i]!!.x + bricks[i]!!.getBrick().width,
                    bricks[i]!!.y + bricks[i]!!.getBrick().height
                )

                if (RectF.intersects(brickRect, ballRect)) {
                    if (bricks[i]!!.x + brickRect.width() / 2 - ball.x < 0.5
                        || ball.x + ballRect.width() / 2 - bricks[i]!!.x < 0.5
                    ) {
                        ball.reverseXVel()
                    }
                    ball.reverseYVel()
                    bricks[i]!!.decreaseLevel()

                    if (Enemy.isAppeared(level) && enemies[i] == null) {
                        enemies[i] = Enemy(
                            level,
                            screenX,
                            screenY,
                            pudding,
                            resources
                        )
                    }
                    playSound(popID)
                    score++
                }
            }
        }

        if (RectF.intersects(ballRect, paddleRect)) {
            ball.setRandomXVel()
            ball.reverseYVel()
            ball.clearObstacleY(paddle.y - 40)
            playSound(paddleID)
        }

        if (ball.y > screenY) {
            playSound(lostID)

            paused = true
            orientationData.pause()

            paddle.reset(screenX, screenY)
            ball.reset(screenX, screenY - paddle.getPaddle().height)
            lives--
            if (lives < 1) return
        }

        if (ball.y < pudding * 4) {
            ball.reverseYVel()
            ball.clearObstacleY(pudding * 4)
            playSound(boundID)
        }

        if (ball.x < pudding) {
            ball.reverseXVel()
            ball.clearObstacleX(pudding)
            playSound(boundID)
        }

        if (ball.x > screenX - ballRect.width() - pudding) {
            ball.reverseXVel()
            ball.clearObstacleX(screenX - ballRect.width() - pudding)
            playSound(boundID)
        }
    }

    private fun draw() {
        if (ourHolder.surface.isValid) {
            canvas = ourHolder.lockCanvas()

            paint.color = ContextCompat.getColor(context, R.color.panelColor)
            canvas.drawRect(0f, 0f, pudding, screenY.toFloat(), paint)
            canvas.drawRect(
                screenX - pudding, 0f,
                screenX.toFloat(), screenY.toFloat(), paint
            )
            canvas.drawRect(0f, 0f, screenX.toFloat(), pudding * 4, paint)

            var background = BitmapFactory
                .decodeResource(resources, backgroundID)
            background = Bitmap
                .createScaledBitmap(
                    background,
                    screenX - pudding.toInt() * 2,
                    screenY - pudding.toInt() * 2,
                    false
                )

            canvas.drawBitmap(background, pudding, pudding * 4, paint)

            canvas.drawBitmap(paddle.getPaddle(), paddle.x, paddle.y, paint)
            canvas.drawBitmap(ball.getBall(), ball.x, ball.y, paint)

            var remBricks = 0

            for (i in 0 until numBricks) {
                if (bricks[i]!!.getVisibility()) {
                    canvas.drawBitmap(
                        bricks[i]!!.getBrick(),
                        bricks[i]!!.x,
                        bricks[i]!!.y,
                        paint
                    )
                    remBricks++
                }

            }

            for (i in 0 until numBricks) {
                if (enemies[i] != null) {
                    canvas.drawBitmap(
                        enemies[i]!!.getEnemy(),
                        enemies[i]!!.x,
                        enemies[i]!!.y,
                        paint
                    )
                }
            }

            paint.color = ContextCompat.getColor(context, R.color.colorAccent)
            paint.textSize = 2f * pudding
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(
                "Lives: $lives      Score: $score",
                pudding * 2, pudding * 3, paint
            )

            if (remBricks == 0) {
                paint.textSize = 7f * pudding
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("WIN!", screenX * 0.5f, screenY * 0.5f, paint)
                gameOver = true
                paused = true

                val storage = ScoresStorage(context)
                if (lives > storage.readRank(level))
                    storage.saveRank(level, lives)
                if (score > storage.readScores(level))
                    storage.saveScores(level, score)
            }

            if (lives < 1) {
                paint.color = Color.RED
                paint.textSize = 5f * pudding
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("GAME OVER", screenX * 0.5f, screenY * 0.5f, paint)
                gameOver = true
                paused = true
            }

            if (paused && !gameOver) {
                paint.textSize = 5f * pudding
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("TOUCH", screenX * 0.5f, screenY * 0.5f, paint)
            }

            ourHolder.unlockCanvasAndPost(canvas)
        }
    }

    abstract fun createBricks()

    private fun updatePaddle() {
        val roll = orientationData.orientation[2] - orientationData.startOrientation[2]
        val xSpeed = roll * screenX / 920f
        paddle.update(xSpeed, timeThisFrame)

        if (paddle.x < pudding) paddle.x = pudding
        if (paddle.x > screenX - paddle.getPaddle().width - pudding)
            paddle.x = screenX - paddle.getPaddle().width - pudding
    }

    private fun playSound(id: Int) {
        soundPool.play(id, 1f, 1f, 0, 0, 1f)
    }

    fun pause() {
        playing = false
        try {
            gameThread!!.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }

    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread!!.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {

        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                orientationData.register()
                paused = false
            }
        }
        return true
    }

}