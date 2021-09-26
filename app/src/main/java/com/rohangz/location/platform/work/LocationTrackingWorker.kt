package com.rohangz.location.platform.work

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.rohangz.location.R
import com.rohangz.location.platform.application.LocationProviderApplication
import com.rohangz.location.platform.extensions.isGpsEnabled
import com.rohangz.location.platform.repository.LocationRepoImpl
import com.rohangz.location.platform.view.LocationListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationTrackingWorker constructor(
    context: Context,
    params: WorkerParameters,
) : Worker(context, params) {
    companion object {
        const val TAG = "LocationTrackingWorker"
        const val CHANNEL_ID = "LocationTrackingWorkerChannel"
        const val NOTIFICATION_ID = 12
    }

    val locationRepo = LocationRepoImpl(context as LocationProviderApplication)

    override fun doWork(): Result {
        try {
            if (applicationContext.isGpsEnabled() && ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                LocationServices.getFusedLocationProviderClient(applicationContext).lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            Log.d(TAG, "latitude: ${it.latitude}, longitude: ${it.longitude}")
                            sendNotification(it.latitude, it.longitude)
                            locationRepo.saveLocation(it.latitude, it.longitude)
                        }
                    }
                    .addOnFailureListener {

                    }
                return Result.success()
            }
            return Result.failure()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            e.printStackTrace()
            return Result.failure()
        }
    }

    private fun sendNotification(latitude: Double, longitude: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.app_notification_name_location_worker),
                NotificationManager.IMPORTANCE_HIGH,
            )
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, LocationListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)


        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.app_location_worker_title))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(applicationContext.getString(R.string.app_tap_here_to_proceed))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }

    }

}