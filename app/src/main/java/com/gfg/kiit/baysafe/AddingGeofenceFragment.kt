package com.gfg.kiit.baysafe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gfg.kiit.baysafe.databinding.FragmentAddingGeofenceBinding
import com.gfg.kiit.baysafe.databinding.FragmentMapsBinding


class AddingGeofenceFragment : Fragment() {

    private var _binding: FragmentAddingGeofenceBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddingGeofenceBinding.inflate(layoutInflater, container, false)
        binding.sharedViewModel=sharedViewModel
        Log.d("AddingGeofence","Hey1")
        binding.buttonDone.setOnClickListener{
            sharedViewModel.geoRadius=binding.slider.value
            sharedViewModel.geofenceReady=true

//            Log.d("AddingGeofence","${sharedViewModel.geoRadius},${sharedViewModel.geoLatLng}")

            findNavController().navigate(R.id.action_addingGeofenceFragment_to_mapsFragment)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}