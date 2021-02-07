package com.owsky.sushihubredone.ui.view

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentConfigureTableBinding
import com.owsky.sushihubredone.ui.viewmodel.CreateTableViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class ConfigureTablePage : Fragment(R.layout.fragment_configure_table) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: CreateTableViewModel by viewModels()
        val binding = FragmentConfigureTableBinding.bind(view)
        binding.apply {
            menuPrice.imeOptions = EditorInfo.IME_ACTION_DONE
            configureDone.setOnClickListener {
                when {
                    configureName.text.isEmpty() ->
                        Toast.makeText(requireContext(), "Insert the restaurant's name", Toast.LENGTH_SHORT).show()
                    menuPrice.text.isEmpty() ->
                        Toast.makeText(requireContext(), "Insert the menu's price", Toast.LENGTH_SHORT).show()
                }
                val restaurantName = configureName.text.toString()
                val price = menuPrice.text.toString().toDouble()
                runBlocking {
                    launch { model.createTable(null, restaurantName, price) }.join()
                }
                findNavController().navigate(R.id.action_configureTableNav_to_generateQRPage)
            }
        }
    }
}