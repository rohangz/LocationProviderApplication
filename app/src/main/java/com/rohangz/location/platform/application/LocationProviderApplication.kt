package com.rohangz.location.platform.application

import android.app.Application
import androidx.room.RoomDatabase
import com.rohangz.location.platform.room.LocationDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

private lateinit var _instance: LocationProviderApplication

@HiltAndroidApp
class LocationProviderApplication : Application() {

    companion object {
        const val TAG = "LocationProviderApplication"

        val Instance: LocationProviderApplication
            get() = _instance
    }

    @Inject
    lateinit var locationDataBase: LocationDatabase

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }
}