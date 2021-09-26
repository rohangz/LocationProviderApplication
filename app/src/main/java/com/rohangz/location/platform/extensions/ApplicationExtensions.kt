package com.rohangz.location.platform.extensions

import android.app.Application
import android.content.Context
import android.location.LocationManager


fun Application.isGpsEnabled(): Boolean {
    return (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
        LocationManager.GPS_PROVIDER
    )
}

fun Context.isGpsEnabled(): Boolean {
    return (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
        LocationManager.GPS_PROVIDER
    )
}