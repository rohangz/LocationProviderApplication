package com.rohangz.location.platform.di.modules

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rohangz.location.platform.application.LocationProviderApplication
import com.rohangz.location.platform.repository.LocationRepoImpl
import com.rohangz.location.platform.room.LocationDatabase
import com.rohangz.location.repository.ILocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {


    companion object {

        const val LOCATION_DATABASE_NAME = "LocationWorkManager.db"

        @Provides
        fun provideApplication(): LocationProviderApplication {
            return LocationProviderApplication.Instance
        }

        @Provides
        fun provideLocationDataBase(application: Application): LocationDatabase {
            return Room.databaseBuilder(
                application,
                LocationDatabase::class.java,
                LOCATION_DATABASE_NAME
            ).build()
        }
    }
}