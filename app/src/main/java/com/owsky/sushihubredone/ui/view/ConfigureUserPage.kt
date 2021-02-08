package com.owsky.sushihubredone.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentConfigureUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfigureUserPage : Fragment(R.layout.fragment_configure_user) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentConfigureUserBinding.bind(view)
        binding.configureUserTextView.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.configureUserBtn.setOnClickListener {
            val username = binding.configureUserTextView.text.toString()
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