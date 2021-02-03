package com.owsky.sushihubredone.view

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.viewmodel.CreateTableViewModel
import java.util.*

class ConfigureTablePage : Fragment(R.layout.fragment_configure_table) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val model: CreateTableViewModel by viewModels()
		val name = view.findViewById<EditText>(R.id.configureName)
		val menuPrice = view.findViewById<EditText>(R.id.menuPrice)
		val done = view.findViewById<Button>(R.id.configureDone)

		menuPrice.imeOptions = EditorInfo.IME_ACTION_DONE
		done.setOnClickListener {
			when {
				name.text.isEmpty() ->
					Toast.makeText(
						requireContext(),
						"Insert the restaurant's name", Toast.LENGTH_SHORT
					).show()
				menuPrice.text.isEmpty() ->
					Toast.makeText(
						requireContext(),
						"Insert the menu's price", Toast.LENGTH_SHORT
					).show()
			}
			val tableCode = UUID.randomUUID().toString()
			val restaurantName = name.text.toString()
			val price = menuPrice.text.toString().toFloat()

			model.createTable(tableCode, restaurantName, price)
//			findNavController().navigate(action)
		}
	}
}