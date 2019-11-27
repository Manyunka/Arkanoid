package com.tsu.arkanoid

import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : Activity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var breakoutEngine: BreakoutEngine
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mediaPlayer = MediaPlayer.create(this, R.raw.level1_theme)
        mediaPlayer.isLooping = true

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val display = windowManager.defaultDisplay
        breakoutEngine = BreakoutEngine(this, display)
        setContentView(breakoutEngine)

    }

    override fun onResume() {
        super.onResume()
        breakoutEngine.resume(sensorManager, sensor)
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        breakoutEngine.pause(sensorManager)
        mediaPlayer.pause()

    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }
}

