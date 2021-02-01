package com.owsky.sushihubredone.view

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
import com.owsky.sushihubredone.viewmodel.CreateTableViewModel

class ConfigureTablePage : Fragment(R.layout.fragment_configure_table) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val model: CreateTableViewModel by viewModels()
		val name = view.findViewById<EditText>(R.id.configureName)
		val menuPrice = view.findViewById<EditText>(R.id.menuPrice)
		val done = view.findViewById<Button>(R.id.configureDone)

		menuPrice.imeOptions = EditorInfo.IME_ACTION_DONE
		done.setOnClickListener {
			if (name.text.isEmpty())
				Toast.makeText(requireContext(), "Insert the restaurant's name", Toast.LENGTH_SHORT)
					.show()
			else if (menuPrice.text.isEmpty())
				Toast.makeText(requireContext(), "Insert the menu's price", Toast.LENGTH_SHORT)
					.show()
			else {
				val price = menuPrice.text.toString().toFloat()
				val restaurantName = name.text.toString()
				model.createTable(restaurantName, price)
//				findNavController().navigate(action)
			}
		}
	}
}