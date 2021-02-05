package com.owsky.sushihubredone.view

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
import com.owsky.sushihubredone.model.entities.Table
import com.owsky.sushihubredone.viewmodel.CreateTableViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GenerateQRPage : Fragment(R.layout.fragment_qr_generator) {
    private lateinit var data: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lateinit var table: Table
        val viewModel: CreateTableViewModel by viewModels()
        runBlocking {
            launch { table = viewModel.getTableInfo()!! }.join()
        }
        data = TextUtils.join(";", listOf(table.id, table.restaurant, table.menuPrice))
        val args: GenerateQRPageArgs by navArgs()
        if (args.share)
            view.findViewById<Button>(R.id.doneqr).visibility = View.GONE
        else
            view.findViewById<Button>(R.id.doneqr).setOnClickListener {
                findNavController().navigate(R.id.action_generateQRPage_to_configureUserPage)
            }
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