package com.example.openstroke

import android.content.Context
import android.location.Location

class MeasuredValues (private val context: Context, private val updateMeasuredValues: UpdateMeasuredValues):
        GPSHelper.LocationUpdateListener,
        StrokeCounter.StrokeListener {

    var strokeCount = 0 // in number
    var strokeTime = System.currentTimeMillis() // In ms
    var distance = 0f // Total distance in m
    var speed = 0f // Speed in m.s-1
    var time = System.currentTimeMillis()  // in ms

    private var lastStrokeTime = System.currentTimeMillis()
    private var lastPoint: Location? = null

    private lateinit var gpsHelper: GPSHelper
    private lateinit var strokeCounter: StrokeCounter

    fun startMeasures() {
        gpsHelper = GPSHelper(context, this)
        strokeCounter = StrokeCounter(context, this)

        gpsHelper.startLocationUpdates()
        strokeCounter.startListening()
    }

    fun stopMeasures() {
        gpsHelper.stopLocationUpdates()
        strokeCounter.stopListening()
    }

    interface UpdateMeasuredValues {
        fun update()
    }

    override fun onStrokeDetected() {
        val tmp = System.currentTimeMillis()
        strokeTime = tmp - lastStrokeTime
        time += strokeTime
        lastStrokeTime = tmp
        strokeCount += 1
        updateMeasuredValues.update()
    }

    override fun onLocationUpdate(location: Location) {
        speed = location.speed // Speed in second/500m
        val  lastPoint = lastPoint
        if (lastPoint != null) distance += location.distanceTo(lastPoint)
        this.lastPoint = location
        updateMeasuredValues.update()
    }

}