package com.gfg.kiit.baysafe

import com.google.android.gms.maps.model.LatLng

data class GeofenceDetails(
    val location:LatLng,
    val geoRadius:Double,
)
