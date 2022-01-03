package com.gfg.kiit.baysafe.data

object DataDetails {

    const val PERMISSION_LOCATION_REQUEST_CODE = 1
    const val PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE = 2
    const val  PERMISSION_INTERNET_REQUEST_CODE=3
    const val NOTIFICATION_CHANNEL_ID="geofence_transition_id"
    const val NOTIFICATION_CHANNEL_NAME="geofence_notification"
    const val NOTIFICATION_ID=3
    var geofencesList= mutableListOf<GeofenceDetails>()
}