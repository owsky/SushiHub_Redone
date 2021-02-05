package com.owsky.sushihubredone.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TableAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListOrders(ListOrders.ListOrdersType.Pending)
            1 -> ListOrders(ListOrders.ListOrdersType.Confirmed)
            else -> ListOrders(ListOrders.ListOrdersType.Delivered)
        }
    }
}