package com.gfg.kiit.baysafe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gfg.kiit.baysafe.data.DataDetails
import com.gfg.kiit.baysafe.data.GeofenceDetails
import com.gfg.kiit.baysafe.data.GeofenceDetails2
import com.gfg.kiit.baysafe.activity.MainActivity2
import com.gfg.kiit.baysafe.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*

class LoadGeofenceFragment : Fragment() {
    private lateinit var database:FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database= FirebaseDatabase.getInstance()
        reference=database.getReference("Users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database.reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                Log.d("LoadGeofence", "hoho ${DataDetails.geofencesList.size}")
                for(data in datasnapshot.children)
                {
                    var model=data.getValue(GeofenceDetails2::class.java)
                    if (model != null) {
                        DataDetails.geofencesList.add(GeofenceDetails(LatLng(model.latitude!!,model.longitude!!),model.geoRadius!!))
                    }
                }
                Log.d("LoadGeofence", "hoho2 ${DataDetails.geofencesList.size}")




                requireActivity().run {

                    startActivity(Intent(this, MainActivity2::class.java))
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("LoadGeofenceFragment","$error")
            }

        })
        return inflater.inflate(R.layout.fragment_load_geofence, container, false)
    }


}