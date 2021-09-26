package com.rohangz.location.platform.repository

import android.util.Log
import com.rohangz.location.platform.application.LocationProviderApplication
import com.rohangz.location.platform.room.LocationEntity
import com.rohangz.location.repository.ILocationRepository
import com.rohangz.location.repository.models.LocationRepoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationRepoImpl @Inject constructor(
    val application: LocationProviderApplication
) : ILocationRepository {

    companion object {
        const val TAG = "LocationRepoImpl"
    }

    override fun saveLocation(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            application.locationDataBase.locationDao().insert(
                LocationEntity(
                    id = 0L,
                    latitude = latitude,
                    longitude = longitude,
                    timeStamp = System.currentTimeMillis()
                )
            )
        }
    }

    override fun getSavedLocations(): Flow<List<LocationRepoModel>> {
        return application.locationDataBase.locationDao().selectAll().map {
            it.map { entity ->
                LocationRepoModel(
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    timeStamp = entity.timeStamp
                )
            }.toList()
        }.flowOn(Dispatchers.IO)
    }
}