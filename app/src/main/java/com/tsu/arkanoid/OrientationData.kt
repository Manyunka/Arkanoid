package com.tsu.arkanoid

import android.content.Context
import android.hardware.SensorManager
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorEvent
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import androidx.core.content.ContextCompat.getSystemService
import android.hardware.SensorEventListener
import android.provider.SyncStateContract


class OrientationData(context: Context) : SensorEventListener {
    private val manager: SensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor
    private val magnometer: Sensor

    private var accelOutput: FloatArray? = null
    private var magOutput: FloatArray? = null

    val orientation = FloatArray(3)

    var startOrientation: FloatArray? = null
        private set

    fun newGame() {
        startOrientation = null
    }

    init {
        accelerometer = manager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnometer = manager.getDefaultSensor(TYPE_MAGNETIC_FIELD)
    }

    fun register() {
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        manager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun pause() {
        manager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == TYPE_ACCELEROMETER)
            accelOutput = event.values
        else if (event.sensor.type == TYPE_MAGNETIC_FIELD)
            magOutput = event.values
        if (accelOutput != null && magOutput != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput)
            if (success) {
                SensorManager.getOrientation(R, orientation)
                if (startOrientation == null) {
                    startOrientation = FloatArray(orientation.size)
                    System.arraycopy(orientation, 0, startOrientation!!, 0, orientation.size)
                }
            }
        }
    }
}