package com.owsky.sushihubredone.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.crazylegend.viewbinding.viewBinding
import com.google.zxing.BarcodeFormat
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.FragmentQrScanBinding
import com.owsky.sushihubredone.ui.viewmodel.CreateTableViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanQRPage : Fragment(R.layout.fragment_qr_scan) {
    private val binding by viewBinding(FragmentQrScanBinding::bind)
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        setupScanner()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val requestCameraLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (!result) {
                    Toast.makeText(requireContext(), "SushiHub requires the Camera permission to scan the QR codes", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
            requestCameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setupScanner() {
        codeScanner.apply {
            formats = listOf(BarcodeFormat.QR_CODE)
            decodeCallback = DecodeCallback {
                val model: CreateTableViewModel by viewModels()
                val info = TextUtils.split(it.text, ";")
                val tableCode = info[0]
                val menuPrice = info[1].toDouble()
                val restName = info[2]
                model.createTable(tableCode, restName, menuPrice)
                findNavController().navigate(R.id.action_scanQRNav_to_configureUserPage)
            }
            errorCallback = ErrorCallback {}
        }
        binding.scannerView.setOnClickListener { codeScanner.startPreview() }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}