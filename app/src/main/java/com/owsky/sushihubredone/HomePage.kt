package com.owsky.sushihubredone

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

class HomePage : Fragment(R.layout.fragment_homepage) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		view.findViewById<Button>(R.id.btnJoin).setOnClickListener { _ -> findNavController().navigate(R.id.action)}
		view.findViewById<Button>(R.id.btnCreate).setOnClickListener { _ -> findNavController().navigate(R.id.action)}

		val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
		prefs.getString("table_code", null)?.let {
			ResumeDialog(it).show(parentFragmentManager, null)
		}
	}

	private class ResumeDialog(tableCode : String) : DialogFragment() {
		private val tableCode  = tableCode

		override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
			val builder = AlertDialog.Builder(requireContext())
			builder.setTitle("Do you want to access the last unfinished session?")
				.setPositiveButton("Yes") { _, _ -> findNavController().navigate(R.id.listsNav) }
				.setNegativeButton("No") {_, _ -> {
					// TODO: implement viewmodel checkout
					dismiss()
				}}
			return builder.create()
		}
	}
}