package com.example.openstroke
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class StrokeCounter(context: Context, private val strokeListener: StrokeListener) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastAcceleration: Float = 0f
    private var lastStrokeTime: Long = 0
    private val strokeThreshold = 10f // Threshold for detecting a stroke
    private val timeThresholdMin = 1000L // Minimum time between strokes in milliseconds
    private val timeThresholdMax = 6000L // Minimum time between strokes in milliseconds

    interface StrokeListener {
        fun onStrokeDetected()
        fun onStrokeStop()
    }

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate the magnitude of acceleration
            val acceleration = sqrt(x * x + y * y + z * z)

            // Calculate the change in acceleration
            val deltaAcceleration = acceleration - lastAcceleration
            lastAcceleration = acceleration

            // Check if the change in acceleration exceeds the threshold and enough time has passed
            val currentTime = System.currentTimeMillis()
            if (deltaAcceleration > strokeThreshold
                && currentTime - lastStrokeTime > timeThresholdMin) {
                strokeListener.onStrokeDetected()
                lastStrokeTime = currentTime
            }
            if (currentTime - lastStrokeTime > timeThresholdMax ) {
                strokeListener.onStrokeStop()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle changes in sensor accuracy if needed
    }
}
