package com.gfg.kiit.baysafe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gfg.kiit.baysafe.databinding.ItemPlacesBinding

class PlacesAdapter : ListAdapter<GeofenceDetails, PlacesAdapter.PlaceViewHolder>(PlaceComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding =
            ItemPlacesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val currItem = getItem(position)
        if (currItem != null) {
            holder.bind(currItem)
        }
    }

    class PlaceViewHolder(private val binding: ItemPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GeofenceDetails) {
            binding.apply {
                latitude.text = item.location.latitude.toString()
                longitude.text = item.location.longitude.toString()
            }
        }
    }

    class PlaceComparator : DiffUtil.ItemCallback<GeofenceDetails>() {
        override fun areItemsTheSame(oldItem: GeofenceDetails, newItem: GeofenceDetails): Boolean {
            if (oldItem.location.latitude == newItem.location.latitude &&
                oldItem.location.longitude == newItem.location.longitude
            )
                return true
            return false
        }

        override fun areContentsTheSame(
            oldItem: GeofenceDetails,
            newItem: GeofenceDetails
        ) = oldItem == newItem

    }
}