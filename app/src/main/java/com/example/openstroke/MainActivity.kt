package com.example.openstroke

import GPSHelper
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), GPSHelper.LocationUpdateListener {

    private lateinit var gpsHelper: GPSHelper
    private lateinit var speedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speedTextView = findViewById(R.id.speedTextView)

        gpsHelper = GPSHelper(this, this)

        if (checkPermissions()) {
            gpsHelper.startLocationUpdates()
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

    @SuppressLint("SetTextI18n")
    override fun onLocationUpdate(location: Location) {
        val speed = 500 / location.speed // Speed in second/500m
        val minutes = (speed / 60).toInt()
        val seconds = (speed % 60).toInt()
        speedTextView.text = "Speed: $minutes m $seconds /500m"
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
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
}
