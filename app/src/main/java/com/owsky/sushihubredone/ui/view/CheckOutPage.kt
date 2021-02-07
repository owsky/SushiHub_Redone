package com.owsky.sushihubredone.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentCheckOutBinding
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class CheckOutPage : Fragment(R.layout.fragment_check_out) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCheckOutBinding.bind(view)

        val viewModel: OrdersViewModel by viewModels()
        val locale = requireActivity().resources.configuration.locales.get(0)
        val prefs = requireActivity().getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        val tableCode = prefs.getString("table_code", null)!!
        var menuPrice = 0.0
        var extraPrice = 0.0
        runBlocking {
            launch { menuPrice = viewModel.getMenuPrice(tableCode) }
            launch { extraPrice = viewModel.getExtraPrice() }
            joinAll()
        }

        val df = DecimalFormat("0", DecimalFormatSymbols.getInstance())
        df.maximumFractionDigits = 340
        binding.apply {
            checkoutMenu.text = String.format(locale, "Menu Price: %s €", df.format(menuPrice))
            checkoutExtra.text = String.format(locale, "Extras Price: %s €", df.format(extraPrice))
            checkoutTotal.text = String.format(locale, "Total: %s €", df.format(menuPrice + extraPrice))
            checkoutDone.setOnClickListener {
                viewModel.checkout(requireActivity(), tableCode)
                findNavController().navigate(R.id.action_checkOutPage_to_homePageNav)
            }
        }
    }
}