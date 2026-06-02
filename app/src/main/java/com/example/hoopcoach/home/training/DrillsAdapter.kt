package com.example.hoopcoach.home.training

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hoopcoach.core.model.Drill
import com.example.hoopcoach.databinding.ItemDrillBinding

class DrillsAdapter(
    private val onItemClick: (Drill) -> Unit = {}
): ListAdapter<Drill, DrillsAdapter.DrillViewHolder>(DIFF){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        p1: Int
    ): DrillViewHolder {
        val binding = ItemDrillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrillViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DrillViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class DrillViewHolder(
        private val binding: ItemDrillBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(drill: Drill){
            binding.tvTitle.text = drill.title
            binding.tvCategory.text = drill.category

            Glide.with(binding.ivCover)
                .load(drill.cover)
                .centerCrop()
                .into(binding.ivCover)

            binding.root.setOnClickListener{
                onItemClick(drill)
            }
        }
    }

    companion object{
        private val DIFF = object : DiffUtil.ItemCallback<Drill>(){
            override fun areItemsTheSame(oldItem: Drill, newItem: Drill) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Drill, newItem: Drill) =
                oldItem == newItem

        }
    }
}

