package com.example.cameraxdemo.utils

import android.content.Context
import com.example.cameraxdemo.R.string
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun createNewFile(
  folder: File,
  format: String,
  fileExtension: String
) =
  File(
      folder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + fileExtension
  )

fun getOutputDirectory(context: Context): File {
  val appContext = context.applicationContext
  val mediaDir = context.externalMediaDirs.firstOrNull()
      ?.let {
        File(it, appContext.resources.getString(
            string.app_name
        )).apply { mkdirs() }
      }
  return if (mediaDir != null && mediaDir.exists())
    mediaDir
  else
    appContext.filesDir
}
