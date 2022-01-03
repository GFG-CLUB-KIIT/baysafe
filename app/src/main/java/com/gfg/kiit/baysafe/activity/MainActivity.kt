package com.gfg.kiit.baysafe.activity

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gfg.kiit.baysafe.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_Fragment) as NavHostFragment
//        val navController = navHostFragment.navController



//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            val dest: String = try {
//                resources.getResourceName(destination.id)
//            } catch (e: Resources.NotFoundException) {
//                destination.id.toString()
//            }
//            Toast.makeText(
//                this@MainActivity, "Navigated to $dest",
//                Toast.LENGTH_SHORT
//            ).show()
//            Log.d("NavigationActivity", "Navigated to $dest")
//        }
    }
}