<h1 align="center"><b>Baysafe</b></h1> 
<h2> Features </h2>
<ol>
 <li> Detects unsafe locations in the user's route either via Push Notifications or shown in map. </li>
 <li> Select and mark an area it as unsafe.</li>
 <li> S.O.S. button on clicking which the user’s location would be shared with users.</li>
</ol>

<h2> Technology </h2>
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
