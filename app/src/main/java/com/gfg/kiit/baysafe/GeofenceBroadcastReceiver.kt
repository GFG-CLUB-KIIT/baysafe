package com.gfg.kiit.baysafe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gfg.kiit.baysafe.Constants.NOTIFICATION_CHANNEL_ID
import com.gfg.kiit.baysafe.Constants.NOTIFICATION_CHANNEL_NAME
import com.gfg.kiit.baysafe.Constants.NOTIFICATION_ID
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //Geofencing event is created with the provided intent
        val geofencingEvent= GeofencingEvent.fromIntent(intent)


        //If any error
        if(geofencingEvent.hasError())
        {
            val errorMessage= GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.d("BroadcastReceiver", errorMessage)
            return
        }

        when(geofencingEvent.geofenceTransition)
        {   //When user Enters the Geofence
            Geofence.GEOFENCE_TRANSITION_ENTER->{
                Log.d("BroadcastReceiver", "Geofence Enter")
                displayNotification(context,"You have entered an Unsafe Zone")
            }

            //When user Exits the Geofence
            Geofence.GEOFENCE_TRANSITION_EXIT->{
                Log.d("BroadcastReceiver", "Geofence Exit")

                displayNotification(context,"You have exited an Unsafe Zone")
            }


            //When user stays in the Geofence for a time period
            Geofence.GEOFENCE_TRANSITION_DWELL->{
                Log.d("BroadcastReceiver", "Geofence present")

                displayNotification(context,"You have been in an Unsafe Zone for a long time")
            }
            else ->{
                Log.d("BroadcastReceiver", "Invalid Type")

                displayNotification(context,"Invalid type")
            }
        }
    }

    private fun displayNotification(context: Context,geofenceTransition:String)
    {
        val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        val notification=NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_geofence_icon)
            .setContentTitle("Baysafe")
            .setContentText(geofenceTransition)
        notificationManager.notify(NOTIFICATION_ID,notification.build())
    }

    //If API is 28 or greater then Notification channel is required
    private fun createNotificationChannel(notificationManager: NotificationManager)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            val channel=NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
               NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}