package com.gfg.kiit.baysafe

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

 class SharedViewModel @Inject constructor(
    application: Application,
): AndroidViewModel(application) {
     val app = application

     private var geofencingClient = LocationServices.getGeofencingClient(app.applicationContext)

    var geoLatLng: LatLng = LatLng(0.0, 0.0)
    var geoRadius: Float = 1f
    var geofenceReady = false
    var geofencePrepared = false
     val geofencesList= mutableListOf<GeofenceDetails>()
    var position=0

    fun resetSharedValues() {

        geoLatLng = LatLng(0.0, 0.0)
        geoRadius = 1f
        geofenceReady = false
        geofencePrepared = false
    }

    fun addGeofence(location:LatLng,radius:Double)
    {
        //Adding to Geofence List
        Log.d("SharedViewModel", radius.toString())
        geofencesList.add(GeofenceDetails(location, radius*1000))

        //Upload on Firebase


    }

     private fun setPendingIntent(geoId: Int): PendingIntent {
         val intent = Intent(app, GeofenceBroadcastReceiver::class.java)
         return PendingIntent.getBroadcast(
             app,
             geoId,
             intent,
             PendingIntent.FLAG_UPDATE_CURRENT
         )
     }
     @SuppressLint("MissingPermission")
     fun startGeofence() {
        if(Permissions.hasBackgroundLocationPermission(app))
        {
            val geofence= Geofence.Builder()
                .setRequestId(position.toString())
                .setCircularRegion(
                    geofencesList[position].location.latitude,
                    geofencesList[position].location.longitude,
                    geofencesList[position].geoRadius.toFloat()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL
                )

                .setLoiteringDelay(1000)
                .build()
            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(
                    GeofencingRequest.INITIAL_TRIGGER_ENTER
                            or GeofencingRequest.INITIAL_TRIGGER_EXIT
                            or GeofencingRequest.INITIAL_TRIGGER_DWELL
                )
                .addGeofence(geofence)
                .build()
            geofencingClient.addGeofences(geofencingRequest, setPendingIntent(geofencesList[position].geoRadius.toInt())).run {
                addOnSuccessListener {
                    Log.d("SharedViewModel", "Successfully added.")
                }
                addOnFailureListener {
                    Log.e("SharedViewModel", it.message.toString())
                }
            }
            position += 1
        } else {
            Log.d("SharedViewModel", "Permission not granted.")
        }
     }

}



