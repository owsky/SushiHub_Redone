package com.owsky.sushihubredone

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController

class CancelDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Quit without saving?")
            .setPositiveButton("OK") { _, _ -> findNavController().navigateUp() }
            .setNegativeButton("Cancel") { _, _ -> dismiss() }
        return builder.create()
    }
}