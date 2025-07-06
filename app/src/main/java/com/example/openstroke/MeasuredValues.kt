package com.example.openstroke

import android.content.Context
import android.location.Location
import java.util.Timer
import java.util.TimerTask

class MeasuredValues (private val context: Context, private val updateMeasuredValues: UpdateMeasuredValues):
        GPSHelper.LocationUpdateListener,
        StrokeCounter.StrokeListener,
        TimerTask() {

    // What to expose for display
    var totalStrokeCount = 0 // in number
    var strokeTime: Long? = null // In ms, null if not rowing
    var totalDistance = 0f // Total distance in m
    var speed = 0f // Speed in m.s-1
    var totalTime = 0L  // in ms

    // Internal variables for computing
    private var lastStrokeTime: Long? = null
    private var lastPoint: Location? = null

   // Instantiation of listeners
    private lateinit var gpsHelper: GPSHelper
    private lateinit var strokeCounter: StrokeCounter
    private lateinit var timeCounter: Timer

    fun startMeasures() {
        gpsHelper = GPSHelper(context, this)
        strokeCounter = StrokeCounter(context, this)
        timeCounter = Timer()

        gpsHelper.startLocationUpdates()
        strokeCounter.startListening()
    }

    fun stopMeasures() {
        gpsHelper.stopLocationUpdates()
        strokeCounter.stopListening()
        timeCounter.cancel()
    }

    interface UpdateMeasuredValues {
        fun update()
    }

    override fun onStrokeDetected() {
        val lastStroke = lastStrokeTime
        totalStrokeCount += 1

        if (lastStroke == null) { // Starts rowing again
            lastStrokeTime = System.currentTimeMillis()
            timeCounter.schedule(this, 0L, 1000L)
            updateMeasuredValues.update()
            return
        }

        val tmp = System.currentTimeMillis()
        strokeTime = tmp - lastStroke
        lastStrokeTime = tmp
        updateMeasuredValues.update()
    }

    override fun onStrokeStop() {
        lastStrokeTime = null
        timeCounter.cancel()
    }

    override fun onLocationUpdate(location: Location) {
        if (lastStrokeTime == null) { // If not rowing, just update loc
            lastPoint = location
            return
        }

        speed = location.speed

        val  lastPoint = lastPoint
        if (lastPoint != null) totalDistance += location.distanceTo(lastPoint)
        this.lastPoint = location

        updateMeasuredValues.update()
    }

    // This is run every second
    override fun run() {
        totalTime += 1
        updateMeasuredValues.update()
    }

}