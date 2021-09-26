package com.rohangz.location.platform.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.rohangz.location.platform.extensions.isGpsEnabled
import com.rohangz.location.platform.model.LocationStatusResponseModel
import com.rohangz.location.repository.ILocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor(
    application: Application,
    val repo: ILocationRepository
) :
    AndroidViewModel(application) {

    private val _isGpsEnabled: Boolean
        get() = getApplication<Application>().isGpsEnabled()

    val locationListFlow = repo.getSavedLocations()

    private val _locationStatusFlow = MutableStateFlow<LocationStatusResponseModel?>(null)
    val locationStatusFlow: Flow<LocationStatusResponseModel?>
        get() = _locationStatusFlow

    val statusText = ObservableField("")

    fun onGpsDetectionFailed(e: Exception) {
        _locationStatusFlow.value = LocationStatusResponseModel.GPSDisabled(e)
    }

    fun checkForLocationPermission() {
        _locationStatusFlow.value = LocationStatusResponseModel.CheckLocationPermission()
    }

    fun startLocationFetch() {
        when(_isGpsEnabled) {
            true -> _locationStatusFlow.value = LocationStatusResponseModel.GPSEnabled()
            false -> _locationStatusFlow.value = LocationStatusResponseModel.GPSDisabled()
        }

    }
    fun stopLocationFetch() {
        _locationStatusFlow.value = LocationStatusResponseModel.StopLocationWork()
    }

    fun setWorkStatus(status: String) {
        statusText.set(status)
    }
}