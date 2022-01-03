package com.gfg.kiit.baysafe.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gfg.kiit.baysafe.databinding.FragmentAboutUsBinding
import com.gfg.kiit.baysafe.databinding.FragmentAccountBinding


class AboutUsFragment : Fragment() {

    private var _binding: FragmentAboutUsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentAboutUsBinding.inflate(inflater, container, false)

        binding.developer1.setOnClickListener {
            val intent =Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.linkedin.com/in/shishir-sharma2001/")
            startActivity(intent)

        }
        binding.developer2.setOnClickListener {


            val intent =Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.linkedin.com/in/duttabhishek0/")
            startActivity(intent)

        }
        binding.developer3.setOnClickListener {
            val intent =Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.linkedin.com/in/abhisek-samantaray-989555195/")
            startActivity(intent)
        }
        return binding.root
    }
}