package com.example.hoopcoach.home.training

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hoopcoach.core.model.Drill
import com.example.hoopcoach.databinding.ItemDrill2Binding // Cuadros
import com.example.hoopcoach.databinding.ItemDrillBinding  // Lista lineal

class DrillsAdapter(
    private val isGrid: Boolean = true, // Por defecto es cuadros
    private val onItemClick: (Drill) -> Unit = {}
): ListAdapter<Drill, RecyclerView.ViewHolder>(DIFF){

    // Definimos qué tipo de vista usar
    override fun getItemViewType(position: Int): Int {
        return if (isGrid) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_GRID) {
            val binding = ItemDrill2Binding.inflate(inflater, parent, false)
            GridViewHolder(binding)
        } else {
            val binding = ItemDrillBinding.inflate(inflater, parent, false)
            ListViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val drill = getItem(position)
        if (holder is GridViewHolder) holder.bind(drill)
        else if (holder is ListViewHolder) holder.bind(drill)
    }

    // ViewHolder para CUADROS (item_drill2)
    inner class GridViewHolder(private val binding: ItemDrill2Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(drill: Drill){
            binding.tvTitle.text = drill.title
            Glide.with(binding.ivCover).load(drill.cover).centerCrop().into(binding.ivCover)
            binding.root.setOnClickListener { onItemClick(drill) }
        }
    }

    // ViewHolder para LISTA (item_drill)
    inner class ListViewHolder(private val binding: ItemDrillBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(drill: Drill){
            binding.tvTitle.text = drill.title
            binding.tvDifficulty.text = drill.difficulty
            Glide.with(binding.ivCover).load(drill.cover).centerCrop().into(binding.ivCover)
            binding.root.setOnClickListener { onItemClick(drill) }
        }
    }

    companion object {
        private const val VIEW_TYPE_GRID = 1
        private const val VIEW_TYPE_LIST = 2
        private val DIFF = object : DiffUtil.ItemCallback<Drill>(){
            override fun areItemsTheSame(oldItem: Drill, newItem: Drill) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Drill, newItem: Drill) = oldItem == newItem
        }
    }
}
