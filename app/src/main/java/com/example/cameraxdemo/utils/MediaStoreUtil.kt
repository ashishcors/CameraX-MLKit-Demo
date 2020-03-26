package com.example.cameraxdemo.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILE_NAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"

fun addImageGetOutputStream(context: Context): OutputStream {
  val contentResolver = context.contentResolver
  val collection =
    if (Build.VERSION.SDK_INT >= 29)
      MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
      MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
  val fileDetails = ContentValues().apply {
    put(
        MediaStore.Images.ImageColumns.DISPLAY_NAME,
        SimpleDateFormat(
            FILE_NAME, Locale.US).format(System.currentTimeMillis()) + PHOTO_EXTENSION
    )
  }
  val fileContentUri =
    contentResolver.insert(collection, fileDetails)
        ?: throw Exception("Null fileContentUri")
  return contentResolver.openOutputStream(fileContentUri)
      ?: throw Exception("null outputStream")
}