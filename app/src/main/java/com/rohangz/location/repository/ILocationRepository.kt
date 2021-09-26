package com.rohangz.location.repository

import com.rohangz.location.repository.models.LocationRepoModel
import kotlinx.coroutines.flow.Flow

interface ILocationRepository {

    fun saveLocation(latitude: Double, longitude: Double)

    fun getSavedLocations(): Flow<List<LocationRepoModel>>
}