package com.gfg.kiit.baysafe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gfg.kiit.baysafe.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            delay(3500)
            findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
        }

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


}