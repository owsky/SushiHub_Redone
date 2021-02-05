package com.owsky.sushihubredone.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.owsky.sushihubredone.databinding.OrderItemBinding
import com.owsky.sushihubredone.model.entities.Order

class OrdersAdapter(private val listOrdersType: ListOrders.ListOrdersType) :
	ListAdapter<Order, OrdersAdapter.OrdersViewHolder>(DiffCallback()) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
		val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return OrdersViewHolder(binding)
	}

	override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
		holder.bind(getItem(position))
	}

	fun getOrderAt(position: Int): Order {
		return getItem(position)
	}

	class OrdersViewHolder(private val binding: OrderItemBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(order: Order) {
			binding.apply {
				orderItemCode.text = order.dish
				orderItemDescription.text = order.desc
				orderItemUser.text = order.user
			}
		}
	}

	class DiffCallback : DiffUtil.ItemCallback<Order>() {
		override fun areItemsTheSame(oldItem: Order, newItem: Order) = oldItem == newItem
		override fun areContentsTheSame(oldItem: Order, newItem: Order) =
			oldItem.dish == newItem.dish && oldItem.desc == newItem.desc &&
					oldItem.status == newItem.status && oldItem.user == newItem.user
	}
}