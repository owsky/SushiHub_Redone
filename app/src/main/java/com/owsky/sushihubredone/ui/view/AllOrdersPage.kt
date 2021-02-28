package com.owsky.sushihubredone.ui.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.crazylegend.viewbinding.viewBinding
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentRecyclerviewBinding
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllOrdersPage : Fragment(R.layout.fragment_recyclerview) {
    val binding by viewBinding(FragmentRecyclerviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: AllOrdersPageArgs by navArgs()
        val viewModel: OrdersViewModel by viewModels()
        val ordersAdapter = OrdersAdapter()
        binding.recyclerView.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
            ItemTouchHelper(viewModel.getRecyclerCallback(requireContext(), ordersAdapter, args.listOrdersType)).attachToRecyclerView(this)
        }
        viewModel.getOrders(args.listOrdersType).observe(requireActivity(), ordersAdapter::submitList)
    }
}