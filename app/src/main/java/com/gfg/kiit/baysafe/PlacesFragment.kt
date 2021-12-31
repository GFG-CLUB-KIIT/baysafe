package com.gfg.kiit.baysafe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gfg.kiit.baysafe.databinding.FragmentPlacesBinding

class PlacesFragment : Fragment() {

    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView : RecyclerView
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val placeAdapter : PlacesAdapter = PlacesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlacesBinding.inflate(inflater,container,false)
        binding.apply {
            recyclerView.apply {
                adapter = placeAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
        return binding.root
    }
}