package com.owsky.sushihubredone.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazylegend.viewbinding.viewBinding
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentCheckOutBinding
import com.owsky.sushihubredone.prefsIdentifier
import com.owsky.sushihubredone.prefsTableCode
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class CheckOutPage : Fragment(R.layout.fragment_check_out) {
    private val binding by viewBinding(FragmentCheckOutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: OrdersViewModel by viewModels()
        val locale = requireActivity().resources.configuration.locales.get(0)
        val menuPrice = viewModel.getMenuPrice()
        val extraPrice = viewModel.getExtraPrice()

        val df = DecimalFormat("0", DecimalFormatSymbols.getInstance())
        df.maximumFractionDigits = 340
        binding.apply {
            checkoutMenu.text = String.format(locale, "Menu Price: %s €", df.format(menuPrice))
            checkoutExtra.text = String.format(locale, "Extras Price: %s €", df.format(extraPrice))
            checkoutTotal.text = String.format(locale, "Total: %s €", df.format(menuPrice + extraPrice))
            checkoutDone.setOnClickListener {
                val prefs = requireActivity().getSharedPreferences(prefsIdentifier, Context.MODE_PRIVATE)
                viewModel.checkout(requireActivity(), prefs.getString(prefsTableCode, null)!!)
                findNavController().navigate(R.id.action_checkOutPage_to_homePageNav)
            }
        }
    }
}