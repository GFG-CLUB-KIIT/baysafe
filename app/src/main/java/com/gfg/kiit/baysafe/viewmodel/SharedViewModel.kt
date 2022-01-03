package com.gfg.kiit.baysafe.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.gfg.kiit.baysafe.data.DataDetails
import com.gfg.kiit.baysafe.data.GeofenceDetails2
import com.gfg.kiit.baysafe.feature.Permissions
import com.gfg.kiit.baysafe.broadcastreceiver.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    private var position =0
    var mDatabaseRef : DatabaseReference = FirebaseDatabase.getInstance().reference




    /*
        Uploading Geofence on Firebase
     */
    /*The Constant.geofence list gets updated automatically everytime you upload data on Firebase*/
    fun addGeofence(location: LatLng, radius: Double) {
        //Upload on Firebase
        DataDetails.geofencesList.clear()
        val model= GeofenceDetails2(location.latitude,location.longitude,radius.toDouble())
        val uniqueID = mDatabaseRef.push().key

        if (uniqueID != null) {
            mDatabaseRef.child(uniqueID).setValue(model).addOnSuccessListener {
                Log.d("SharedViewModel", "Geofence successfully added")
            }
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

        /*Activating the Geofence*/
    @SuppressLint("MissingPermission")
    fun startGeofence(location: LatLng,radius: Float) {
        if (Permissions.hasBackgroundLocationPermission(app)) {
            position= DataDetails.geofencesList.size-1
            val geofence = Geofence.Builder()
                .setRequestId(position.toString())
                .setCircularRegion(
                    location.latitude,
                    location.longitude,
                    radius
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
                setPendingIntent(position)
            ).run {
                addOnSuccessListener {

                    Log.d("SharedViewModel", "Successfully added.")
                }
                addOnFailureListener {

                }
            }

        } else {
            Log.d("SharedViewModel", "Permission not granted.")
        }
    }

}



