package com.owsky.sushihubredone.ui.view

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.data.entities.Table
import com.owsky.sushihubredone.ui.viewmodel.CreateTableViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class GenerateQRPage : Fragment(R.layout.fragment_qr_generator) {
    private lateinit var data: String
    private val viewModel: CreateTableViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: GenerateQRPageArgs by navArgs()
        if (args.share)
            view.findViewById<Button>(R.id.doneqr).visibility = View.GONE
        else
            view.findViewById<Button>(R.id.doneqr).setOnClickListener { findNavController().navigate(R.id.action_generateQRPage_to_configureUserPage) }
            val table = viewModel.getCurrentTable()!!
            data = TextUtils.join(";", listOf(table.id, table.restaurant, table.menuPrice))
            generateQR()
    }

    private fun generateQR() {
        val qrSize = 177
        val imageView = requireView().findViewById<ImageView>(R.id.qr_code)
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, qrSize, qrSize)
        val bitmap = Bitmap.createBitmap(qrSize, qrSize, Bitmap.Config.RGB_565)

        for (x in 0 until qrSize) {
            for (y in 0 until qrSize) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        imageView.setImageBitmap(bitmap)
    }
}