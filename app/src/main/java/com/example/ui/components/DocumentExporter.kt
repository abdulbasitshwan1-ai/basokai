package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object DocumentExporter {

    fun shareAsText(context: Context, title: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "📄 $title\n\n$content\n\n-- دروستکراوە لە ڕێگەی BASOKA AI")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "هاوبەشکردنی بەڵگەنامە لەگەڵ:")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun exportAsFormattedFile(context: Context, title: String, content: String): File? {
        return try {
            val safeName = title.replace("[^a-zA-Z0-9ء-ي]".toRegex(), "_").take(30)
            val fileName = "${safeName}_BASOKA.txt"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                val header = "==============================\n$title\nبڵاوکراوەی فەرمی BASOKA AI\n==============================\n\n"
                out.write((header + content).toByteArray(Charsets.UTF_8))
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}
