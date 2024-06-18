package com.cambassador.app.ui

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cambassador.app.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.Date


class Utility {
    companion object {
        @Composable
        fun stringToDate(it: String): Date {
            val sdf = SimpleDateFormat(stringResource(R.string.date_pattern))
            return sdf.parse(it)
        }

        @Composable
        fun dateToString(date: Date): String {
            val sdf = SimpleDateFormat(stringResource(R.string.date_pattern))
            return sdf.format(date)
        }

        //QRcodeImageCreater
        fun qrCode(data: String): Bitmap? {
            val bitMatrix = createBitMatrix(data)
            return try {
                bitMatrix?.let { createBitmap(it) }
            } catch (e: Exception) {
                e.message?.let { Log.d("bitmap_error", data) }
                null
            }
        }

        private fun createBitMatrix(data: String): BitMatrix? {
            val multiFormatWriter = MultiFormatWriter()
            val hints = mapOf(
                EncodeHintType.MARGIN to 0,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L
            )
            return multiFormatWriter.encode(
                data,
                BarcodeFormat.QR_CODE,
                700,
                700,
                hints
            )
        }

        private fun createBitmap(bitMatrix: BitMatrix): Bitmap {
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        }
    }
}
