package com.tsu.arkanoid

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.view.WindowManager




class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private val LEVEL_1_ID = 1
    private val LEVEL_2_ID = 2
    private val LEVEL_3_ID = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mediaPlayer = MediaPlayer.create(this, R.raw.main_sound)
        mediaPlayer.isLooping = true

        /*val set = AnimatorInflater.loadAnimator(this, R.animator.bubble_trans_down) as AnimatorSet
        val set2 = AnimatorInflater.loadAnimator(this, R.animator.bubble_trans_up) as AnimatorSet
        set.setTarget(imageView)
        set2.setTarget(imageView2)
        set.start()
        set2.start()*/

        val gameIntent = Intent(this, GameActivity::class.java)

        level1.setOnClickListener {
            gameIntent.putExtra("LEVEL_ID", LEVEL_1_ID)
            startActivity(gameIntent)
        }

        level2.setOnClickListener {
            gameIntent.putExtra("LEVEL_ID", LEVEL_2_ID)
            startActivity(gameIntent)
        }

        level3.setOnClickListener {
            gameIntent.putExtra("LEVEL_ID", LEVEL_3_ID)
            startActivity(gameIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }

}
