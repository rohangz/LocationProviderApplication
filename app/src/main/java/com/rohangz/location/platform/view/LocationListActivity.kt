package com.rohangz.location.platform.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.rohangz.location.R
import com.rohangz.location.databinding.ActivityLocationBinding
import com.rohangz.location.platform.CONST_TIME_UNIT
import com.rohangz.location.platform.CONST_WORK_LENGTH
import com.rohangz.location.platform.adapters.LocationListAdapter
import com.rohangz.location.platform.model.LocationStatusResponseModel
import com.rohangz.location.platform.viewmodel.LocationListViewModel
import com.rohangz.location.platform.work.LocationTrackingWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class LocationListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LocationListActivity"
        const val CONST_RESOLVE_API_EXCEPTION = 1000
    }

    private lateinit var _binding: ActivityLocationBinding

    @Inject
    lateinit var locationRequest: LocationRequest

    @Inject
    lateinit var adapter: LocationListAdapter
    private var _alertDialog: AlertDialog? = null
    private val _viewModel: LocationListViewModel by viewModels()

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when (isGranted) {
                true -> onLocationPermissionGranted()
                else -> showLocationIsNecessaryDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        _binding.viewModel = _viewModel
        _binding.locationList.adapter = adapter
        _binding.locationList.layoutManager = LinearLayoutManager(this)
        lifecycleScope.launchWhenStarted {
            _viewModel.locationStatusFlow.collect { data ->
                when (data) {
                    is LocationStatusResponseModel.GPSEnabled -> onGpsEnabled()
                    is LocationStatusResponseModel.GPSDisabled -> onGpsDisabled(data.exception)
                    is LocationStatusResponseModel.LocationSetup -> onLocationSetup()
                    is LocationStatusResponseModel.StopLocationWork -> onStopLocationWork()
                    is LocationStatusResponseModel.CheckLocationPermission -> checkForLocationPermissionAndProceed()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            _viewModel.locationListFlow.collect {
                adapter.updateList(it)
            }
        }
        WorkManager.getInstance(applicationContext)
            .getWorkInfosByTagLiveData(LocationTrackingWorker.TAG).observe(this) {
                if(!it.isNullOrEmpty()) {
                    _viewModel.setWorkStatus(it[0].state.name)
                }
        }
    }

    private fun checkForLocationPermissionAndProceed() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                onLocationPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                showLocationIsNecessaryDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    private fun onLocationPermissionGranted() {
        _viewModel.startLocationFetch()
    }

    private fun showLocationIsNecessaryDialog() {
        _alertDialog?.dismiss()
        _alertDialog = AlertDialog.Builder(this)
            .setTitle(application.getString(R.string.app_location_permission_required_title))
            .setMessage(application.getString(R.string.app_location_permission_required_description))
            .setPositiveButton(application.getString(R.string.app_btn_ok)) { v, di ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setOnDismissListener { _alertDialog = null }
            .setCancelable(false)
            .create()
        _alertDialog?.show()

    }


    private fun onGpsEnabled() {
        val periodicWorker =
            PeriodicWorkRequestBuilder<LocationTrackingWorker>(
                CONST_WORK_LENGTH,
                CONST_TIME_UNIT
            ).addTag(
                LocationTrackingWorker.TAG
            ).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            LocationTrackingWorker.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorker
        )
    }

    private fun onGpsDisabled(e: Exception?) {
        when (e) {
            null -> onLocationSetup()
            is ResolvableApiException -> e.startResolutionForResult(
                this,
                CONST_RESOLVE_API_EXCEPTION
            )
            else -> {
                val intent = Intent()
                intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                startActivity(intent)
            }
        }

    }

    private fun onLocationSetup() {
        LocationServices.getSettingsClient(application)
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)
                    .build()
            )
            .addOnSuccessListener {
                _viewModel.startLocationFetch()
            }
            .addOnFailureListener {
                _viewModel.onGpsDetectionFailed(it)
            }
    }

    private fun onStopLocationWork() {
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag(LocationTrackingWorker.TAG)
    }

}