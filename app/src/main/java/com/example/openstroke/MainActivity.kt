package com.example.openstroke

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), GPSHelper.LocationUpdateListener,
    StrokeCounter.StrokeListener {

    // GPS values
    private lateinit var gpsHelper: GPSHelper
    private lateinit var speedTextView: TextView

    // Stroke counter
    private lateinit var strokeCounter: StrokeCounter
    private var strokeCount = 0
    private lateinit var strokeCountTextView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speedTextView = findViewById(R.id.speedTextView)
        gpsHelper = GPSHelper(this, this)


        strokeCountTextView = findViewById(R.id.strokeCountTextView)
        strokeCounter = StrokeCounter(this, this)

        if (checkPermissions()) {
            gpsHelper.startLocationUpdates()
            strokeCounter.startListening()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onStrokeDetected() {
        strokeCount++
        runOnUiThread {
            "Stroke Count: $strokeCount".also { strokeCountTextView.text = it }
        }
    }

    override fun onLocationUpdate(location: Location) {
        val speed = 500 / location.speed // Speed in second/500m
        val minutes = (speed / 60).toInt()
        val seconds = (speed % 60).toInt()
        "Speed: $minutes m $seconds /500m".also { speedTextView.text = it }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                gpsHelper.startLocationUpdates()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gpsHelper.stopLocationUpdates()
        strokeCounter.stopListening()
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
}
