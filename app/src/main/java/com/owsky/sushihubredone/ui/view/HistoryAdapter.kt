package com.owsky.sushihubredone.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.owsky.sushihubredone.data.entities.Table
import com.owsky.sushihubredone.databinding.HistoryItemBinding

class HistoryAdapter : ListAdapter<Table, HistoryAdapter.TableViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        return TableViewHolder(HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
        val action = HistoryPageDirections.actionHistoryNavToHistoryDetails(getItem(position))
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(action))
    }

    class TableViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(table: Table) {
            binding.apply {
                historyItemDate.text = table.dateCreation.toString()
                historyItemName.text = table.restaurant
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Table>() {
        override fun areItemsTheSame(oldItem: Table, newItem: Table): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Table, newItem: Table): Boolean =
            oldItem.dateCreation == newItem.dateCreation && oldItem.restaurant == newItem.restaurant && oldItem.menuPrice == newItem.menuPrice
    }
}