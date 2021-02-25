package com.owsky.sushihubredone.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.databinding.FragmentUserInputBinding
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertOrderPage : Fragment(R.layout.fragment_user_input) {
    private lateinit var binding: FragmentUserInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                CancelDialog().show(parentFragmentManager, tag)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserInputBinding.bind(view)
        binding.switchExtra.setOnCheckedChangeListener { _, _ -> flipExtra() }
        binding.addDesc.imeOptions = EditorInfo.IME_ACTION_DONE

        binding.saveAndQuit.setOnClickListener {
            if (saveOrder())
                findNavController().navigateUp()
        }

        binding.saveAndNew.setOnClickListener {
            if (saveOrder()) {
                binding.apply {
                    binding.addCode.text = null
                    binding.addDesc.text = null
                    binding.addQuantity.text = null
                    binding.addPrice.text = null
                    binding.addCode.requestFocus()
                }
            }
        }
    }

    private fun saveOrder(): Boolean {
        val prefs = requireActivity().getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        val viewModel: OrdersViewModel by viewModels()
        val tableCode = prefs.getString("table_code", null)!!
        val dishCode = binding.addCode.text.toString()
        val status = OrderStatus.Pending
        val description = binding.addDesc.text.toString()
        var extraPrice = 0.0
        if (binding.addPrice.text.isNotEmpty()) {
            extraPrice = binding.addPrice.text.toString().toDouble()
        }

        when {
            dishCode.trim().isEmpty() ->
                Toast.makeText(requireContext(), "Insert the dish code", Toast.LENGTH_SHORT).show()
            binding.addQuantity.text.toString().isEmpty() ->
                Toast.makeText(requireContext(), "Insert the quantity", Toast.LENGTH_SHORT).show()
            else -> {
                val username = prefs.getString("username", null)!!
                val newOrder = Order(dishCode, description, status, tableCode, username, false, extraPrice)
                viewModel.insertOrder(newOrder, binding.addQuantity.text.toString().toInt())
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Undo?", BaseTransientBottomBar.LENGTH_LONG)
                    .setAction("Undo?") { viewModel.undoInsert() }
                    .setAnchorView(requireActivity().findViewById(R.id.saveAndQuit)).show()
                return true
            }
        }
        return false
    }

    private fun flipExtra() {
        binding.apply {
            if (addPrice.isVisible) {
                addPrice.isVisible = false
                priceTextView.isVisible = false
                addDesc.imeOptions = EditorInfo.IME_ACTION_DONE
            } else {
                priceTextView.isVisible = true
                addPrice.isVisible = true
                addDesc.nextFocusDownId = binding.addPrice.id
                addDesc.imeOptions = EditorInfo.TYPE_NULL
            }
        }
    }
}