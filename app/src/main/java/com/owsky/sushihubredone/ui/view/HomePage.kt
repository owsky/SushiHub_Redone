package com.owsky.sushihubredone.ui.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazylegend.viewbinding.viewBinding
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentHomepageBinding
import com.owsky.sushihubredone.prefsIdentifier
import com.owsky.sushihubredone.prefsTableCode
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint

class HomePage : Fragment(R.layout.fragment_homepage) {
    private val binding by viewBinding(FragmentHomepageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnJoin.setOnClickListener { findNavController().navigate(R.id.action_homePageNav_to_scanQRNav) }
            btnCreate.setOnClickListener { findNavController().navigate(R.id.action_homePageNav_to_configureTableNav) }
        }
        val prefs = requireActivity().getSharedPreferences(prefsIdentifier, Context.MODE_PRIVATE)
        if (prefs.contains(prefsTableCode))
            ResumeDialog(prefs.getString(prefsTableCode, null)!!).show(parentFragmentManager, null)
    }

    @AndroidEntryPoint
    class ResumeDialog(private val tableCode: String) : DialogFragment() {

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