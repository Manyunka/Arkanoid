package com.tsu.arkanoid.view

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tsu.arkanoid.R
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }, 2800)


        val ballAnim = AnimatorInflater.loadAnimator(
            this,
            R.animator.ball_trans_up
        ) as AnimatorSet
        ballAnim.setTarget(splashBall)
        ballAnim.start()

        val logoAnim1 =
            AnimatorInflater.loadAnimator(
                this,
                R.animator.brick_rotate_left
            ) as AnimatorSet
        logoAnim1.setTarget(logo1)
        logoAnim1.start()

        val logoAnim2 =
            AnimatorInflater.loadAnimator(
                this,
                R.animator.brick_rotate_right
            ) as AnimatorSet
        logoAnim2.setTarget(logo2)
        logoAnim2.start()

    }
}
