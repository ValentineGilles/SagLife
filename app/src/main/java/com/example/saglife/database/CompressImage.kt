package com.example.saglife.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

suspend fun compressImage(uri: Uri, context: Context): ByteArray {
    val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }

    val outputStream = ByteArrayOutputStream()
    originalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Ajustez la qualité ici
    return outputStream.toByteArray()
}
