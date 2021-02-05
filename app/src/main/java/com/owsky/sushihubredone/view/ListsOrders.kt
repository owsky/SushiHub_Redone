package com.owsky.sushihubredone.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentRecyclerviewBinding
import com.owsky.sushihubredone.viewmodel.OrdersViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ListOrders(private val listOrdersType: ListOrdersType) :
    Fragment(R.layout.fragment_recyclerview) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRecyclerviewBinding.bind(view)
        val ordersAdapter = OrdersAdapter(listOrdersType)
        val viewModel: OrdersViewModel by viewModels()
        val prefs = requireActivity().getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        lateinit var callback: ItemTouchHelper.SimpleCallback
        runBlocking {
            launch { callback = viewModel.getRecyclerCallback(requireContext(), ordersAdapter, listOrdersType) }.join()
        }
        binding.apply {
            recyclerView.apply {
                adapter = ordersAdapter
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                callback.let { ItemTouchHelper(it).attachToRecyclerView(recyclerView) }
            }
        }
        viewModel.getOrders(listOrdersType).observe(requireActivity(), ordersAdapter::submitList)
    }

//	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//		super.onCreateOptionsMenu(menu, inflater)
//		arguments?.let {
//			val args = ListOrdersArgs.fromBundle(it)
//			if (args.listOrdersType == ListOrdersType.History)
//				inflater.inflate(R.menu.storico_overflow, menu)
//			menu.findItem(R.id.deleteTable).isVisible = true
//		}
//	}

    enum class ListOrdersType {
        Pending,
        Confirmed,
        Delivered,
        Synchronized
    }
}