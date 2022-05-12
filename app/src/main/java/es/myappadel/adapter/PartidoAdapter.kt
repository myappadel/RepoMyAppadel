package es.myappadel.adapter

import android.view.ViewGroup
import es.myappadel.clases.Partido
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.myappadel.databinding.ItemMatchBinding

class PartidoAdapter(val clickItem: ClickItem): ListAdapter<Partido, PartidoAdapter.ViewHolder>(PartidoDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: Partido, clickItem: ClickItem) {

            binding.partido = item
            binding.clickItem = clickItem
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMatchBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class PartidoDiffCallback : DiffUtil.ItemCallback<Partido>() {

    override fun areItemsTheSame(oldItem: Partido, newItem: Partido): Boolean {
        return oldItem.uid == newItem.uid
    }


    override fun areContentsTheSame(oldItem: Partido, newItem: Partido): Boolean {
        return oldItem == newItem
    }


}

class ClickItem(val clickItem: (item: Partido) -> Unit) {
    fun onClick(item: Partido) = clickItem(item)
}