package com.khaled.nearbyapp.ui.view

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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.khaled.nearbyapp.R
import com.khaled.nearbyapp.databinding.ActivityMainBinding
import com.khaled.nearbyapp.ui.viewmodel.MainViewModel
import com.khaled.nearbyapp.utils.orFalse


class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var venueListAdapter: VenueListAdapter
    private var locationRequest: LocationRequest? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setObservers()
        setupRecyclerView()
        setupLocationService()
        setViewsListeners()
    }

    private fun setViewsListeners() {
        binding.realTimeToggle.setOnCheckedChangeListener { _, isChecked -> viewModel.isRealTimeUpdates = isChecked }
    }

    private fun setupRecyclerView() {
        venueListAdapter = VenueListAdapter()
        binding.venueRecyclerView.adapter = venueListAdapter
        binding.venueRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    val visibleItemCount = binding.venueRecyclerView.layoutManager?.childCount ?: 0
                    val totalItemCount = binding.venueRecyclerView.layoutManager?.itemCount ?: Int.MAX_VALUE
                    val pastVisibleItems =
                        (binding.venueRecyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                            ?: 0
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) viewModel.loadNextPage()
                }
            }
        })
    }

    private fun setObservers() {
        viewModel.venueListLiveData.observe(this, { venueList ->
            binding.loadingContainer.visibility = View.GONE
            if (venueList.isEmpty()) {
                binding.statusImageView.setImageResource(R.drawable.ic_no_data)
                binding.statusTextView.text = getString(R.string.no_data_found)
                binding.statusContainer.visibility = View.VISIBLE
            } else {
                binding.statusContainer.visibility = View.GONE
                venueListAdapter.setVenueList(venueList)
            }
        })
        viewModel.showMessage.observe(this, {
            binding.loadingContainer.visibility = View.GONE
            if (viewModel.venueListLiveData.value.isNullOrEmpty().orFalse()) {
                binding.statusImageView.setImageResource(R.drawable.ic_error)
                binding.statusTextView.text = getString(R.string.something_went_wrong)
                binding.statusContainer.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                binding.statusContainer.visibility = View.GONE
            }
        })
        viewModel.notifyVenueList.observe(this, { position ->
            position?.let { venueListAdapter.notifyItemChanged(position, "") }
        })
        viewModel.venueListProgressBarEndlessLoading.observe(this, { isShow ->
            binding.progressBarEndlessLoading.visibility = if (isShow.orFalse()) View.VISIBLE else View.GONE
        })
    }

    private fun setupLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val message = "location = ${location?.latitude} ${location?.longitude}"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    viewModel.onLocationChanged(location)
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        viewModel.onLocationChanged(location)
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
        private const val LOCATION_INTERVAL_REFRESH_TIME = 1 * 60 * 1000L
        private const val LOCATION_REFRESH_DISTANCE = 10f
        private const val LOCATION_REQUEST_CODE = 1000
    }
}