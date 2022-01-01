package com.gfg.kiit.baysafe

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.gfg.kiit.baysafe.databinding.FragmentEmergencyBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class EmergencyFragment : Fragment() {
    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLatitude=0.0
    private var userLongitude=0.0
    private val callback = OnMapReadyCallback { googleMap ->
        Log.d("Emergency1","hello $userLatitude $userLongitude")
        val yourLocation = LatLng(userLatitude, userLongitude)
        googleMap.addMarker(MarkerOptions().position(yourLocation).title("Marker in your Location"))
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(userLatitude,userLongitude),15f),1000,null
        )
    }
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(isLocationEnabled()) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


            fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    if (location != null) {
                        userLatitude=location.latitude
                        userLongitude=location.longitude
                        Log.d("Emergency","$userLatitude $userLongitude")
                    }


                    val geocoder=Geocoder(requireContext(), Locale.getDefault())
                    val address: Address?
                    val addresses: List<Address> =geocoder.getFromLocation(userLatitude, userLongitude, 1)

                    if (addresses.isNotEmpty()) {
                        address = addresses[0]
                        binding.emergencyUserLocation.text = address.getAddressLine(0)
                    } else{
                        binding.emergencyUserLocation.text="Turn on your GPS"
                    }
                    val mapFragment = childFragmentManager.findFragmentById(R.id.emergencyMap) as SupportMapFragment?
                    mapFragment?.getMapAsync(callback)

                }


        }
    }
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        // Inflate the layout for this fragment
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)


        binding.emergencyButton.setOnClickListener{
            sendMessage()
        }

        return binding.root
    }


    private fun isLocationEnabled():Boolean
    {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
    private fun sendMessage()
    {
        val message=binding.emergencyText.text.toString()
        val sendIntent:Intent=Intent().apply {
            action=Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "http://maps.google.com/maps?q=$userLatitude,$userLongitude $message")
            type = "text/plain"
           setPackage("com.whatsapp")
        }
        startActivity(sendIntent)

    }
}