package com.gfg.kiit.baysafe.data

import com.google.android.gms.maps.model.LatLng

 data class GeofenceDetails(
     val location: LatLng,
     val geoRadius:Double
)

