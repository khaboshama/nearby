package com.khaled.nearbyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class MainActivity : AppCompatActivity(), LocationListener {
    private var locationRequest: LocationRequest? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val message = "location = ${location?.latitude} ${location?.longitude}"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }
            }
        }
        handleStartLocationService()
    }

    private fun handleStartLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermissionIsNotGranted()) {
                requestLocationPermission()
                return
            }
        }
        requestLocationUpdates()
    }

    private fun checkLocationPermissionIsNotGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )
    }

    private fun isGpsEnabled(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun isNetworkEnabled(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (checkAllPermissionGranted(grantResults)) {
                requestLocationUpdates()
            } else {
                displayLocationEnabledErrorMessage()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (isNetworkEnabled().not() && isGpsEnabled().not()) {
            displayLocationEnabledErrorMessage()
            return
        }
        locationRequest?.let {
            fusedLocationClient.requestLocationUpdates(
                it,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun displayLocationEnabledErrorMessage() {
        Toast.makeText(this, "Please enable network and gps, then try again", Toast.LENGTH_LONG).show()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_INTERVAL_REFRESH_TIME
            fastestInterval = LOCATION_INTERVAL_REFRESH_TIME
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        locationRequest?.smallestDisplacement = LOCATION_REFRESH_DISTANCE
    }

    private fun checkAllPermissionGranted(grantResults: IntArray): Boolean {
        val permissionGranted = grantResults.filter { it == PackageManager.PERMISSION_GRANTED }
        return permissionGranted.size == grantResults.size
    }

    override fun onLocationChanged(location: Location) {
        val message = "location = ${location.latitude} ${location.longitude}"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.i("khaled", message)
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(this, "enabled provider = $provider", Toast.LENGTH_LONG).show()
        requestLocationUpdates()
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(this, "disabled provider = $provider", Toast.LENGTH_LONG).show()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    companion object {
        private const val LOCATION_INTERVAL_REFRESH_TIME = 2 * 60 * 1000L
        private const val LOCATION_REFRESH_DISTANCE = 10f
        private const val LOCATION_REQUEST_CODE = 1000
    }
}