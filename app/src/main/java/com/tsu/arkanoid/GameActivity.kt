package com.tsu.arkanoid

import android.os.Bundle
import android.app.Activity
import android.media.MediaPlayer
import android.view.WindowManager
import android.content.Intent


class GameActivity : Activity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var breakoutEngine: BreakoutEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val display = windowManager.defaultDisplay

        when (intent.extras?.getInt("LEVEL_ID")) {
            1 -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.level1_theme)
                breakoutEngine = Level1View(this, display)
            }
            2 -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.level2_theme)
                breakoutEngine = Level2View(this, display)
            }
            3 -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.level3_theme)
                breakoutEngine = Level3View(this, display)
            }
            else -> mediaPlayer = MediaPlayer.create(this, R.raw.level2_theme)
        }

        mediaPlayer.isLooping = true

        setContentView(breakoutEngine)

    }

    override fun onResume() {
        super.onResume()
        breakoutEngine.resume()
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        breakoutEngine.pause()
        mediaPlayer.pause()

    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        super.onBackPressed()
    }
}

