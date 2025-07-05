package com.example.openstroke

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), MeasuredValues.UpdateMeasuredValues {

    private lateinit var measuredValues: MeasuredValues

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        measuredValues = MeasuredValues(this, this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (checkPermissions()) {
            requestPermissions()
        } else {
            measuredValues.startMeasures()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                measuredValues.startMeasures()
            }
        }
    }


    override fun update() {
        val speedTextView: TextView = findViewById(R.id.speedTextView)
        val strokeCountTextView: TextView = findViewById(R.id.strokeCountTextView)
        val distanceTextView: TextView = findViewById(R.id.distanceTextView)

        val speed = 500 / measuredValues.speed // Speed in second/500m
        val minutes = (speed / 60).toInt()
        val seconds = (speed % 60).toInt()

        runOnUiThread {
            "Stroke Count: ${measuredValues.strokeTime}".also { strokeCountTextView.text = it }
            "Speed: $minutes m $seconds /500m".also { speedTextView.text = it }
            "Distance: ${measuredValues.distance}".also { distanceTextView.text = it }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        measuredValues.stopMeasures()
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

}
