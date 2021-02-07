package com.owsky.sushihubredone.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.ui.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertOrderPage : Fragment(R.layout.fragment_user_input) {
    private lateinit var code: EditText
    private lateinit var desc: EditText
    private lateinit var quantity: EditText
    private lateinit var price: EditText
    private lateinit var priceTextView: TextView
    private var isExtra = false

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
        code = view.findViewById(R.id.addCode)
        desc = view.findViewById(R.id.addDesc)
        quantity = view.findViewById(R.id.addQuantity)
        price = view.findViewById(R.id.addPrice)
        priceTextView = view.findViewById(R.id.priceTextView)
        val saveAndQuit = view.findViewById<Button>(R.id.saveAndQuit)
        val saveAndNew = view.findViewById<Button>(R.id.saveAndNew)
        val switchMaterial = view.findViewById<SwitchMaterial>(R.id.switchExtra)
        switchMaterial.setOnCheckedChangeListener { _, _ -> flipExtra() }

        if (!isExtra)
            desc.imeOptions = EditorInfo.IME_ACTION_DONE

        saveAndQuit.setOnClickListener {
            if (saveOrder())
                findNavController().navigateUp()
        }

        saveAndNew.setOnClickListener {
            if (saveOrder()) {
                code.text = null
                desc.text = null
                quantity.text = null
                price.text = null
                code.requestFocus()
            }
        }
    }

    private fun saveOrder(): Boolean {
        val prefs = requireActivity().getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        val viewModel: OrdersViewModel by viewModels()
        val tableCode = prefs.getString("table_code", null)!!
        val dishCode = code.text.toString()
        val status = OrderStatus.Pending
        val description = desc.text.toString()
        var extraPrice = 0.0
        if (price.text.isNotEmpty()) {
            extraPrice = price.text.toString().toDouble()
        }

        when {
            dishCode.trim().isEmpty() ->
                Toast.makeText(requireContext(), "Insert the dish code", Toast.LENGTH_SHORT).show()
            quantity.text.toString().isEmpty() ->
                Toast.makeText(requireContext(), "Insert the quantity", Toast.LENGTH_SHORT).show()
            else -> {
                val username = prefs.getString("username", null)!!
                val newOrder = Order(dishCode, description, status, tableCode, username, false, extraPrice)
                viewModel.insertOrder(newOrder, quantity.text.toString().toInt())
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Undo?", BaseTransientBottomBar.LENGTH_LONG)
                    .setAction("Undo?") { viewModel.undoInsert() }
                    .setAnchorView(requireActivity().findViewById(R.id.saveAndQuit)).show()
                return true
            }
        }
        return false
    }

    private fun flipExtra() {
        if (isExtra) {
            priceTextView.isVisible = false
            price.isVisible = false
            isExtra = false
            desc.imeOptions = EditorInfo.IME_ACTION_DONE
        } else {
            priceTextView.isVisible = true
            price.isVisible = true
            isExtra = true
            desc.nextFocusDownId = price.id
            desc.imeOptions = EditorInfo.TYPE_NULL
        }
    }
}