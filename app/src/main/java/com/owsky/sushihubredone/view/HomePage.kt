package com.owsky.sushihubredone.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentHomepageBinding
import com.owsky.sushihubredone.viewmodel.OrdersViewModel

class HomePage : Fragment(R.layout.fragment_homepage) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomepageBinding.bind(view)
        binding.apply {
            btnJoin.setOnClickListener { findNavController().navigate(R.id.action_homePageNav_to_scanQRNav) }
            btnCreate.setOnClickListener { findNavController().navigate(R.id.action_homePageNav_to_configureTableNav) }
        }

        val prefs = requireActivity().getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        prefs.getString("table_code", null)?.let {
            ResumeDialog(it).show(parentFragmentManager, null)
        }
    }

    class ResumeDialog(tableCode: String) : DialogFragment() {
        private val tableCode = tableCode

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Do you want to resume the last unfinished session?")
                .setPositiveButton("Yes") { _, _ -> findNavController().navigate(R.id.tablePage) }
                .setNegativeButton("No") { _, _ ->
                    run {
                        val viewModel: OrdersViewModel by viewModels()
                        viewModel.checkout(requireActivity(), tableCode)
                    }
                }
            return builder.create()
        }
    }
}