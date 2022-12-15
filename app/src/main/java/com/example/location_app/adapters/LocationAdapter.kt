package com.example.location_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.location_app.Model.LocationEntity
import com.example.location_app.databinding.ActivityMainBinding
import com.example.location_app.databinding.LocationUtemBinding

class LocationAdapter(val context: Context) : ListAdapter<LocationEntity, ViewHolder>(FlowerDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =  LocationUtemBinding.inflate(LayoutInflater.from(context))
      return ViewHolder1(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val locationEntity = getItem(position)
        (holder as ViewHolder1).bind(locationEntity)
    }

    inner class ViewHolder1(val binding: LocationUtemBinding): ViewHolder(binding.root) {
        fun bind(pos: LocationEntity?) {
             binding.locationText.text = pos?.latitude.toString()+" "
             binding.localityText.text = pos?.subLocality.toString()+" "
        }
        }

    }

    object FlowerDiffCallback : DiffUtil.ItemCallback<LocationEntity>() {
        override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem.id == newItem.id
        }
    }


