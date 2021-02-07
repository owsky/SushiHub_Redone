package com.owsky.sushihubredone.ui.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentRecyclerviewBinding
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ListOrders() : Fragment(R.layout.fragment_recyclerview) {
    private var listOrdersType: ListOrdersType? = null

    constructor(listOrdersType: ListOrdersType) : this() {
        this.listOrdersType = listOrdersType
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRecyclerviewBinding.bind(view)
        val viewModel: OrdersViewModel by viewModels()
        if (listOrdersType == null) {
            val arg: ListOrdersArgs by navArgs()
            listOrdersType = arg.listOrdersType
        }
        val ordersAdapter = OrdersAdapter(listOrdersType!!)
        lateinit var callback: ItemTouchHelper.SimpleCallback
        runBlocking {
            launch { callback = viewModel.getRecyclerCallback(requireContext(), ordersAdapter, listOrdersType!!) }.join()
        }
        binding.apply {
            recyclerView.apply {
                adapter = ordersAdapter
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                callback.let { ItemTouchHelper(it).attachToRecyclerView(recyclerView) }
            }
        }
        viewModel.getOrders(listOrdersType!!).observe(requireActivity(), ordersAdapter::submitList)
    }

//	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//		super.onCreateOptionsMenu(menu, inflater)
//		arguments?.let {
//			val args = ListOrdersArgs.fromBundle(it)
//			if (args.listOrdersType == ListOrdersType.History)
//				inflater.inflate(R.menu.history_overflow, menu)
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