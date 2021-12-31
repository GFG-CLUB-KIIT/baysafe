package com.gfg.kiit.baysafe

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class SharedViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {
    val app = application

    private var geofencingClient = LocationServices.getGeofencingClient(app.applicationContext)

    var geoLatLng: LatLng = LatLng(0.0, 0.0)
    var geoRadius: Float = 1f
    var geofenceReady = false
    var geofencePrepared = false
    val geofencesList = mutableListOf<GeofenceDetails>()
    var position = 0

    var mDatabaseRef : DatabaseReference = FirebaseDatabase.getInstance().reference


    fun resetSharedValues() {

        geoLatLng = LatLng(0.0, 0.0)
        geoRadius = 1f
        geofenceReady = false
        geofencePrepared = false
    }

    fun addGeofence(location: LatLng, radius: Double) {
        //Adding to Geofence List
        geofencesList.add(GeofenceDetails(location, radius))

        //Upload on Firebase
        val uniqueID = mDatabaseRef.push().key
        if (uniqueID != null) {
            mDatabaseRef.child(uniqueID).setValue(location)
        }
    }
    fun getAddressFromLatLng(context : Context, location: LatLng) : String{
        val addresses : List<Address>
        val geocoder : Geocoder = Geocoder(context, Locale.getDefault())

        try{
            addresses = geocoder.getFromLocation(location.latitude,location.longitude,1)
            return addresses[0].getAddressLine(0)
        }catch (e : Exception){
            e.printStackTrace()
            return  ""
        }
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
        if (Permissions.hasBackgroundLocationPermission(app)) {
            val geofence = Geofence.Builder()
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

                .setLoiteringDelay(5000)
                .build()
            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(
                    GeofencingRequest.INITIAL_TRIGGER_ENTER
                            or GeofencingRequest.INITIAL_TRIGGER_EXIT
                            or GeofencingRequest.INITIAL_TRIGGER_DWELL
                )
                .addGeofence(geofence)
                .build()
            geofencingClient.addGeofences(
                geofencingRequest,
                setPendingIntent(geofencesList[position].geoRadius.toInt())
            ).run {
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



