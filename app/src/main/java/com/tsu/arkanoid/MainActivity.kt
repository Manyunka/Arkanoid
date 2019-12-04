package com.tsu.arkanoid

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
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

        val storage = ScoresStorage(applicationContext)

        var scores = 0

        val rankImages = arrayOf(rankView1, rankView2, rankView3)

        for (i in 0 until 3) {
            scores += storage.readScores(i + 1)

            when (storage.readRank(i + 1)) {
                1 -> rankImages[i].setImageResource(rank1)
                2 -> rankImages[i].setImageResource(rank2)
                3 -> rankImages[i].setImageResource(rank3)
                else -> rankImages[i].setImageResource(rank0)
            }
        }

        bestScore.text = "Рекорд:\n$scores"


        val gameIntent = Intent(this, GameActivity::class.java)

        level1.setOnClickListener {
            gameIntent.putExtra("LEVEL_ID", LEVEL_1_ID)
            startActivity(gameIntent)
            finish()
        }

        level2.setOnClickListener {
            gameIntent.putExtra("LEVEL_ID", LEVEL_2_ID)
            startActivity(gameIntent)
            finish()
        }

        level3.setOnClickListener {
            gameIntent.putExtra("LEVEL_ID", LEVEL_3_ID)
            startActivity(gameIntent)
            finish()
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

    companion object {
        val rank0 = R.drawable.rank0
        val rank1 = R.drawable.rank1
        val rank2 = R.drawable.rank2
        val rank3 = R.drawable.rank3
    }
}
