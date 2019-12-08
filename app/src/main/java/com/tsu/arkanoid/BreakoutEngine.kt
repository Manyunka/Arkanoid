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
import com.tsu.arkanoid.model.gameObjects.*
import com.tsu.arkanoid.model.gameObjects.bricks.Brick

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

    private var bullets = arrayOfNulls<Bullet>(2)


    private var balls = arrayOfNulls<Ball>(3)
    private var numBalls = 0

    private var enemies = arrayOfNulls<Enemy>(100)

    private var bonuses = arrayOfNulls<Bonus>(100)

    var bricks = arrayOfNulls<Brick>(100)
    var numBricks = 0


    private var hearts = Array(3)
    { Heart(pudding, pudding / 2, 85, 85, 0, resources) }
    private var lives = 3
    private var score = 0

    private var soundPool: SoundPool
    private var popID: Int
    private var boundID: Int
    private var paddleID: Int
    private var bonusID: Int
    private var laserID: Int
    private var enemyID: Int
    private var lostID: Int

    private var orientationData: OrientationData

    init {
        val size = Point()
        gameDisplay.getSize(size)

        screenX = size.x
        screenY = size.y

        paddle = Paddle(screenX, screenY, resources)

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
        bonusID = soundPool.load(context, R.raw.bonus, 0)
        laserID = soundPool.load(context, R.raw.laser, 0)
        enemyID = soundPool.load(context, R.raw.enemy, 0)
        lostID = soundPool.load(context, R.raw.lost, 0)

        create()
    }

    private fun create() {
        paddle.reset(screenX, screenY)

        balls[0] = (Ball(
            screenX, screenY,
            paddle.x + paddle.getPaddle().width / 2, 30, resources
        ))
        balls[0]!!.reset(screenX, screenY - paddle.getPaddle().height)
        pudding = balls[0]!!.getBall().width.toFloat()
        numBalls = 1

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
        updatePaddle()

        for (j in bullets.indices) {
            if (bullets[j] != null) {
                bullets[j]!!.update(fps)

                val bulletRect = RectF(
                    bullets[j]!!.x, bullets[j]!!.y,
                    bullets[j]!!.x + bullets[j]!!.getBullet().width,
                    bullets[j]!!.y + bullets[j]!!.getBullet().height
                )

                if (bullets[j]!!.y < 4 * pudding) {
                    bullets[j] = null
                } else {
                    for (i in 0 until numBricks) {
                        if (bricks[i]!!.getVisibility()) {
                            val brickRect = RectF(
                                bricks[i]!!.x, bricks[i]!!.y,
                                bricks[i]!!.x + bricks[i]!!.getBrick().width,
                                bricks[i]!!.y + bricks[i]!!.getBrick().height
                            )

                            if (RectF.intersects(brickRect, bulletRect)) {
                                bullets[j] = null
                                bricks[i]!!.setInvisible()

                                if (Enemy.isAppeared(level) && enemies[i] == null) {
                                    enemies[i] = Enemy(
                                        level,
                                        screenX,
                                        screenY,
                                        pudding,
                                        resources
                                    )
                                }

                                if (Bonus.isAppeared() && bonuses[i] == null) {
                                    bonuses[i] = Bonus(
                                        screenX,
                                        screenY,
                                        pudding,
                                        resources
                                    )
                                }
                                playSound(popID, 1)
                                score += bricks[i]!!.level
                            }
                        }

                        if (enemies[i] != null) {

                            val enemyRect = RectF(
                                enemies[i]!!.x, enemies[i]!!.y,
                                enemies[i]!!.x + enemies[i]!!.getEnemy().width,
                                enemies[i]!!.y + enemies[i]!!.getEnemy().height
                            )
                            if (RectF.intersects(enemyRect, bulletRect)
                            ) {
                                bullets[j] = null
                                enemies[i] = null
                                playSound(enemyID, 1)
                                score += 10
                            }

                        }
                    }
                }
            }
        }

        for (i in 0 until numBricks) {
            if (enemies[i] != null) {
                enemies[i]!!.update(fps)
                if (enemies[i]!!.y > screenY) {
                    lives--
                    enemies[i] = null
                    playSound(lostID, 1)
                    if (lives < 1) return
                }
            }
            if (bonuses[i] != null) {
                bonuses[i]!!.update(fps)
                if (bonuses[i]!!.y > screenY) {
                    bonuses[i] = null
                }
            }
        }



        for (j in balls.indices) {
            if (balls[j] != null) {
                balls[j]!!.update(fps)
                val ballRect = RectF(
                    balls[j]!!.x, balls[j]!!.y,
                    balls[j]!!.x + balls[j]!!.getBall().width,
                    balls[j]!!.y + balls[j]!!.getBall().height
                )
                val paddleRect = RectF(
                    paddle.x, paddle.y,
                    paddle.x + paddle.getPaddle().width,
                    paddle.y + paddle.getPaddle().height
                )

                for (i in 0 until numBricks) {
                    if (enemies[i] != null) {

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
                            playSound(enemyID, 1)
                            score += 10
                        }
                    }

                    if (bonuses[i] != null) {
                        val bonusRect = RectF(
                            bonuses[i]!!.x, bonuses[i]!!.y,
                            bonuses[i]!!.x + bonuses[i]!!.getBonus().width,
                            bonuses[i]!!.y + bonuses[i]!!.getBonus().height
                        )
                        if (RectF.intersects(bonusRect, paddleRect)) {
                            playSound(bonusID, 1)
                            when (bonuses[i]!!.getTypeBonus()) {
                                EXPAND -> {
                                    paddle.changeWidth()
                                }
                                DIVIDE -> {
                                    for (k in balls.indices)
                                        if (balls[k] == null) {
                                            balls[k] = Ball(
                                                screenX,
                                                screenY,
                                                paddle.x + paddleRect.width() / 2,
                                                Ball.generateAngle(),
                                                resources
                                            )
                                            numBalls++
                                        }
                                }
                                LASER -> {
                                    bullets[0] = Bullet(
                                        screenY,
                                        paddle.x + 2,
                                        resources
                                    )
                                    bullets[1] = Bullet(
                                        screenY,
                                        paddle.x + paddleRect.width() - bullets[0]!!.getBullet().width - 3,
                                        resources
                                    )
                                    playSound(laserID, 2)
                                }
                                PLAYER -> {
                                    if (lives < 3)
                                        lives++
                                }
                            }
                            bonuses[i] = null
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
                            if (bricks[i]!!.x + brickRect.width() / 2 - balls[j]!!.x < 0.5
                                || balls[j]!!.x + ballRect.width() / 2 - bricks[i]!!.x < 0.5
                            ) {
                                balls[j]!!.reverseXVel()
                            }
                            balls[j]!!.reverseYVel()
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

                            if (Bonus.isAppeared() && bonuses[i] == null) {
                                bonuses[i] = Bonus(
                                    screenX,
                                    screenY,
                                    pudding,
                                    resources
                                )
                            }
                            playSound(popID, 1)
                            score++
                        }
                    }
                }

                if (RectF.intersects(ballRect, paddleRect)) {
                    balls[j]!!.setRandomXVel()
                    balls[j]!!.reverseYVel()
                    balls[j]!!.clearObstacleY(paddle.y - 40)
                    playSound(paddleID, 0)
                }

                if (balls[j]!!.y < pudding * 4) {
                    balls[j]!!.reverseYVel()
                    balls[j]!!.clearObstacleY(pudding * 4)
                    playSound(boundID, 0)
                }

                if (balls[j]!!.x < pudding) {
                    balls[j]!!.reverseXVel()
                    balls[j]!!.clearObstacleX(pudding)
                    playSound(boundID, 0)
                }

                if (balls[j]!!.x > screenX - ballRect.width() - pudding) {
                    balls[j]!!.reverseXVel()
                    balls[j]!!.clearObstacleX(screenX - ballRect.width() - pudding)
                    playSound(boundID, 0)
                }

                if (balls[j]!!.y > screenY) {
                    if (numBalls > 1) {
                        balls[j] = null
                        numBalls--
                    } else {
                        playSound(lostID, 1)

                        paused = true
                        orientationData.pause()

                        paddle.reset(screenX, screenY)
                        balls[j]!!.reset(screenX, screenY - paddle.getPaddle().height)
                        lives--
                        if (lives < 1) return
                    }
                }
            }
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

            for (i in balls.indices)
                if (balls[i] != null)
                    canvas.drawBitmap(balls[i]!!.getBall(), balls[i]!!.x, balls[i]!!.y, paint)

            for (i in bullets.indices) {
                if (bullets[i] != null)
                    canvas.drawBitmap(
                        bullets[i]!!.getBullet(),
                        bullets[i]!!.x,
                        bullets[i]!!.y,
                        paint
                    )
            }

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

            for (i in 0 until numBricks) {
                if (bonuses[i] != null) {
                    canvas.drawBitmap(
                        bonuses[i]!!.getBonus(),
                        bonuses[i]!!.x,
                        bonuses[i]!!.y,
                        paint
                    )
                }
            }


            paint.color = ContextCompat.getColor(context, R.color.colorAccent)
            paint.textSize = 2f * pudding
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                "Score: $score",
                screenX - pudding * 2, pudding * 3, paint
            )

            for (i in 0 until 3) {
                if (i < lives)
                    hearts[i].changeType(1)
                else hearts[i].changeType(0)
                canvas.drawBitmap(
                    hearts[i].getHeart(),
                    hearts[i].x + (pudding + hearts[i].getHeart().width) * i,
                    hearts[i].y,
                    paint
                )
            }

            val storage = ScoresStorage(context)
            if (remBricks == 0) {
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 5f * pudding
                canvas.drawText("Score: $score", screenX * 0.5f, screenY * 0.5f, paint)

                paint.textSize = 7f * pudding
                canvas.drawText("WIN!", screenX * 0.5f, screenY * 0.4f, paint)
                gameOver = true
                paused = true

                if (lives > storage.readRank(level))
                    storage.saveRank(level, lives)
                if (score > storage.readScores(level))
                    storage.saveScores(level, score)
            }

            if (lives < 1) {
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 5f * pudding
                canvas.drawText("Score: $score", screenX * 0.5f, screenY * 0.5f, paint)

                paint.color = Color.RED
                canvas.drawText("GAME OVER", screenX * 0.5f, screenY * 0.4f, paint)
                gameOver = true
                paused = true

                if (score > storage.readScores(level))
                    storage.saveScores(level, score)
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

    private fun playSound(id: Int, priority: Int) {
        soundPool.play(id, 1f, 1f, priority, 0, 1f)
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

    companion object {
        const val EXPAND = 0
        const val DIVIDE = 1
        const val LASER = 2
        const val PLAYER = 3
    }

}