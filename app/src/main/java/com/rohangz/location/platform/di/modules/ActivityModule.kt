package com.rohangz.location.platform.di.modules

import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.rohangz.location.platform.adapters.LocationListAdapter
import com.rohangz.location.platform.application.LocationProviderApplication
import com.rohangz.location.platform.repository.LocationRepoImpl
import com.rohangz.location.repository.ILocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {

    companion object {
        @Provides
        fun provideFusedLocationProviderClient(activity: FragmentActivity): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(activity)
        }

        @Provides
        fun provideLocationRequest(): LocationRequest {
            return LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 3 * 1000
                fastestInterval = 5 * 1000
            }
        }

        @Provides
        fun provideListAdapter(): LocationListAdapter {
            return LocationListAdapter(ArrayList(), LocationProviderApplication.Instance)
        }

        @Provides
        fun provideLocationRepo(repo: LocationRepoImpl): ILocationRepository {
            return repo
        }
    }
}