package com.owsky.sushihubredone.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.databinding.FragmentUserInputBinding
import com.owsky.sushihubredone.prefsIdentifier
import com.owsky.sushihubredone.prefsTableCode
import com.owsky.sushihubredone.prefsUsername
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertOrderPage : Fragment(R.layout.fragment_user_input) {
    private val binding by viewBinding(FragmentUserInputBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            CancelDialog().show(parentFragmentManager, tag)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            switchExtra.setOnCheckedChangeListener { _, _ -> flipExtra() }
            addDesc.imeOptions = EditorInfo.IME_ACTION_DONE

            saveAndQuit.setOnClickListener {
                if (saveOrder())
                    findNavController().navigateUp()
            }

            saveAndNew.setOnClickListener {
                if (saveOrder()) {
                    apply {
                        addCode.text = null
                        addDesc.text = null
                        addQuantity.text = null
                        addPrice.text = null
                        addCode.requestFocus()
                    }
                }
            }
        }
    }

    private fun saveOrder(): Boolean {
        val prefs = requireActivity().getSharedPreferences(prefsIdentifier, Context.MODE_PRIVATE)
        val viewModel: OrdersViewModel by viewModels()
        val tableCode = prefs.getString(prefsTableCode, null)!!
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
                val username = prefs.getString(prefsUsername, null)!!
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