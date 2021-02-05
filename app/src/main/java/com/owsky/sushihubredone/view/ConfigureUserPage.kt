package com.owsky.sushihubredone.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.owsky.sushihubredone.R

class ConfigureUserPage : Fragment(R.layout.fragment_configure_user) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val setUsername = view.findViewById<EditText>(R.id.configureUserTextView)
		val button = view.findViewById<Button>(R.id.configureUserBtn)
		setUsername.imeOptions = EditorInfo.IME_ACTION_DONE
		button.setOnClickListener {
			val username = setUsername.text.toString()
			if (username.trim().isEmpty())
				Toast.makeText(requireContext(), "Insert a username", Toast.LENGTH_SHORT).show()
			else {
				val prefs = requireActivity().getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE).edit()
				prefs.putString("username", username).apply()
				findNavController().navigate(R.id.action_configureUserPage_to_tablePage)
			}
		}
	}
}