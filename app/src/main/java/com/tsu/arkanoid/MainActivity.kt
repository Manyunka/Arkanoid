package com.tsu.arkanoid

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.*


class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.main_sound)

        /*val set = AnimatorInflater.loadAnimator(this, R.animator.bubble_trans_down) as AnimatorSet
        val set2 = AnimatorInflater.loadAnimator(this, R.animator.bubble_trans_up) as AnimatorSet
        set.setTarget(imageView)
        set2.setTarget(imageView2)
        set.start()
        set2.start()*/

        level1Button.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
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
