package com.gfg.kiit.baysafe.ui

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gfg.kiit.baysafe.data.DataDetails
import com.gfg.kiit.baysafe.feature.Permissions.hasBackgroundLocationPermission
import com.gfg.kiit.baysafe.feature.Permissions.requestsBackgroundLocationPermission
import com.gfg.kiit.baysafe.R
import com.gfg.kiit.baysafe.databinding.FragmentMapsBinding
import com.gfg.kiit.baysafe.viewmodel.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMapLongClickListener,EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var clicked=false
    private var userLatitude=0.0
    private var userLongitude=0.0
    private  lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val toBottom2: Animation by lazy{AnimationUtils.loadAnimation(requireContext(),
        R.anim.to_bottom2
    )}
    private val toBottom: Animation by lazy{AnimationUtils.loadAnimation(requireContext(),
        R.anim.to_bottom
    )}
    private val fromBottom: Animation by lazy{AnimationUtils.loadAnimation(requireContext(),
        R.anim.from_bottom
    )}

    private val sharedViewModel: SharedViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)

        binding.expandMenu.setOnClickListener{
            onAddButtonClicked()
        }

        binding.addGeoFenceButton.setOnClickListener{

            findNavController().navigate(R.id.action_mapsFragment_to_addingGeofenceFragment)
        }
        binding.alertButton2.setOnClickListener{
            findNavController().navigate(R.id.action_mapsFragment_to_emergencyFragment)
        }



        return binding.root
    }
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    userLatitude=location.latitude
                    userLongitude=location.longitude
                }

                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(this)


            }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }
    override fun onMapReady(googleMap: GoogleMap) {
        map= googleMap
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))
        map.setOnMapLongClickListener(this)
        markingGeofence()
        onGeofenceReady()
        zoomtoLocation()

    }

    private fun markingGeofence() {

            map.clear()
            for (i in DataDetails.geofencesList) {
                drawCircle(LatLng(i.location.latitude, i.location.longitude), i.geoRadius)
                drawMarker(LatLng(i.location.latitude, i.location.longitude))
            }

    }


    private fun onGeofenceReady() {
       if(sharedViewModel.geofenceReady)
       {
           sharedViewModel.geofenceReady=false
           sharedViewModel.geofencePrepared=true
           displayMessage()
           zoomtoLocation()
       }
    }

    private fun displayMessage() {
        lifecycleScope.launch {
            binding.infoMessageTextView.visibility=View.VISIBLE
            delay(2000)
            binding.infoMessageTextView.animate().alpha(0f).duration = 800
            delay(1000)
            binding.infoMessageTextView.visibility=View.INVISIBLE
        }
    }


    override fun onMapLongClick(location: LatLng) {
        if(hasBackgroundLocationPermission(requireContext()))
        {
            if(sharedViewModel.geofencePrepared)
            {   Log.d("Map1","Before Geofence Added")
                setupGeofence(location,sharedViewModel.geoRadius*1000)
            }
            else
            {
                Toast.makeText(
                    requireContext(),
                    "You need to create a Geofence first.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else
        {
            requestsBackgroundLocationPermission(this)
        }
    }

    private fun setupGeofence(location: LatLng,radius: Float) {
        lifecycleScope.launch {
            Log.d("Map1", "Setting Up Geofence")

            drawCircle(location, radius.toDouble())
            drawMarker(location)
            //Adding Geofence to the database and Firebase
             sharedViewModel.addGeofence(location, radius.toDouble())

            //Starting Geofence
            sharedViewModel.startGeofence(location, radius)
            //Zooming to Location

            zoomtoGeofence(location)
        }
    }
    private fun zoomtoLocation() {


        map.addMarker(MarkerOptions().position(LatLng(userLatitude,userLongitude)))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(userLatitude,userLongitude),15f),1000,null
        )
    }
    private fun zoomtoGeofence(location: LatLng) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location,15f),1000,null
        )
    }

    private fun drawCircle(location: LatLng,radius:Double) {
        map.addCircle(
            CircleOptions().center(location).radius(radius)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
                .fillColor(ContextCompat.getColor(requireContext(), R.color.red))
        )
    }

    private fun drawMarker(location: LatLng) {
        map.addMarker(
            MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestsBackgroundLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onGeofenceReady()
        Toast.makeText(
            requireContext(),
            "Permission Granted! Long Press on the Map to add a Geofence.",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked

    }

    private fun setAnimation(clicked:Boolean) {
        if(!clicked)
        {
            binding.addGeoFenceButton.startAnimation(fromBottom)
            binding.alertButton2.startAnimation(fromBottom)
        }
        else
        {

            binding.addGeoFenceButton.startAnimation(toBottom2)
            binding.alertButton2.startAnimation(toBottom)
        }
    }

    private fun setVisibility(clicked:Boolean) {
        if(!clicked)
        {
            binding.addGeoFenceButton.isClickable=true
            binding.alertButton2.isClickable=true
            binding.addGeoFenceButton.visibility=View.VISIBLE
            binding.alertButton2.visibility=View.VISIBLE
        }
        else
        {
            binding.addGeoFenceButton.isClickable=false
            binding.alertButton2.isClickable=false
            binding.addGeoFenceButton.visibility=View.INVISIBLE
            binding.alertButton2.visibility=View.INVISIBLE
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}