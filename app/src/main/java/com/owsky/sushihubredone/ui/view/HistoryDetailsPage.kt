package com.owsky.sushihubredone.ui.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.crazylegend.viewbinding.viewBinding
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentRecyclerviewBinding
import com.owsky.sushihubredone.ui.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailsPage : Fragment(R.layout.fragment_recyclerview) {
    private val viewModel: HistoryViewModel by viewModels()
    private val args: HistoryDetailsPageArgs by navArgs()
    private val binding by viewBinding(FragmentRecyclerviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val ordersAdapter = OrdersAdapter()
        binding.recyclerView.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }

        viewModel.getOrders(args.table.id).observe(requireActivity(), ordersAdapter::submitList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.history_overflow, menu)
        menu.findItem(R.id.deleteTable).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteTable) {
            viewModel.deleteTable(args.table)
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}