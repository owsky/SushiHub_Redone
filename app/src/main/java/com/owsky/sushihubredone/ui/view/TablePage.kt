package com.owsky.sushihubredone.ui.view

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.owsky.sushihubredone.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TablePage : Fragment(R.layout.fragment_table) {
    private lateinit var prefs: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ExitDialog().show(parentFragmentManager, tag)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        prefs = requireActivity().getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            findNavController().navigate(R.id.action_tablePage_to_insertOrderPage)
        }

        val viewPager2 = view.findViewById<ViewPager2>(R.id.viewPager)
        viewPager2.adapter = TableAdapter(this)
        viewPager2.isUserInputEnabled = false
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Pending"
                    tab.setIcon(R.drawable.ic_pending)
                }
                1 -> {
                    tab.text = "Confirmed"
                    tab.setIcon(R.drawable.ic_confirmed)
                }
                2 -> {
                    tab.text = "Delivered"
                    tab.setIcon(R.drawable.ic_delivered)
                }
            }
        }
        tabLayoutMediator.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lista_overflow, menu)
        val item = menu.findItem(R.id.toAllOrders)
        if (!prefs.contains("is_master"))
            item.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.showQR -> {
                val action = TablePageDirections.actionTablePageToGenerateQRPage(true)
                findNavController().navigate(action)
            }
            R.id.toAllOrders -> {
                val action = TablePageDirections.actionTablePageToAllOrders(ListOrders.ListOrdersType.Synchronized)
                findNavController().navigate(action)
            }
            R.id.toCheckout -> {
                findNavController().navigate(R.id.action_tablePage_to_checkOutPage)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class ExitDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Do you want to quit the app?")
            builder.setPositiveButton("OK") { _, _ ->
                requireActivity().finish()
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }
            return builder.create()
        }
    }
}