<h1 align="center"><b>Baysafe</b></h1> 
<p align="center">
  <img src ="https://user-images.githubusercontent.com/56694152/147945415-a0cb2d5e-51d4-4005-91dc-fd7af3080764.jpeg" width="150" height="300" />
  <img src ="https://user-images.githubusercontent.com/56694152/147945419-97857436-f590-48a0-90cf-ed6795461e30.jpeg" width="150" height="300" />
  <img src ="https://user-images.githubusercontent.com/56694152/147945432-cabf8977-1484-48f0-bad0-5e015557c726.jpeg" width="150" height="300" />
  <img src ="https://user-images.githubusercontent.com/56694152/147945436-5ff75db5-d18a-4915-866d-62d605b0d2a6.jpeg" width="150" height="300" />
  <img src ="https://user-images.githubusercontent.com/56694152/147945445-864c34e9-4ebe-4d59-bbe2-53547da21214.jpeg" width="150" height="300" />
 
</p>
<h2> Features </h2>
<ol>
 <li> Detects unsafe locations in the user's route either via Push Notifications or shown in map. </li>
 <li> Select and mark an area it as unsafe.</li>
 <li> S.O.S. button on clicking which the user’s location would be shared with users.</li>
</ol>

<h2> Tech Stack </h2>
<ul>
  <li> Native Android Kotlin </li>
  <li> UI/UX - Figma OR Adobe XD </li>
  <li> Google Places SDK </li>
  <li> Navigation </li>
  <li> View Mode </li>
  <li> Live Data </li>
  <li> Data Store Preferences  </li>
  <li> Data Binding </li>
  <li> Coroutines </li>
  <li> Retrofit </li>
</ul>

<h2> Working </h2>
<ul>
  <li> The core of the app is the Google-Places SDK. A user can create a geofence, with a selected radius(in the range of 1km-10km) to mark it as unsafe. Once marked unsafe, 
a red region forms around the geofence. </li>
  <li> The corresponding Latitude and Longitude and radius gets stored in an object of class GeofenceDetails, as well as gets uploaded to the Firebase Realtime-Database. </li>
</ul>
<h3> Activating the Geofence </h3>
The data is actually the longitude and latitude of the region, and radius of the Geofence. This is how the geofence is activated:

```kotlin
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
```

<h3> Uploading the data</h3>
The following snippet of code is used to add the selected region to firebase

```kotlin
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
```

