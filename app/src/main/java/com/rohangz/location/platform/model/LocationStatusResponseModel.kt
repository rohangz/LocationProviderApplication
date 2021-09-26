package com.rohangz.location.platform.model

sealed class LocationStatusResponseModel {
    class CheckLocationPermission : LocationStatusResponseModel()
    class GPSEnabled : LocationStatusResponseModel()
    class GPSDisabled(val exception: Exception? = null) : LocationStatusResponseModel()
    class LocationSetup : LocationStatusResponseModel()
    class StopLocationWork : LocationStatusResponseModel()
}