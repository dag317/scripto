package com.example.scripto.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.scripto.R
import com.example.scripto.database.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val scanBtn = findViewById<Button>(R.id.scanBtn)

        logoutBtn.setOnClickListener {
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit { clear() }
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        scanBtn.setOnClickListener {
            Toast.makeText(this, "Отправка на сервер...", Toast.LENGTH_SHORT).show()

            val testFile = getFileFromAssets("test_notes.jpg")

            if (testFile != null && testFile.exists()) {
                sendImageToOcr(testFile)
            } else {
                Toast.makeText(this, "Файл test_notes.jpg не найден в assets!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getFileFromAssets(fileName: String): File? {
        return try {
            val cacheFile = File(cacheDir, fileName)
            assets.open(fileName).use { inputStream ->
                FileOutputStream(cacheFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            cacheFile
        } catch (e: Exception) {
            Log.e("OCR_TEST", "Ошибка чтения assets: ${e.message}")
            null
        }
    }

    private fun sendImageToOcr(imageFile: File) {
        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.uploadOcrImage(body)

                showResultDialog(response.corrected_text)

            } catch (e: Exception) {
                Log.e("OCR_TEST_ERROR", "Сервер вернул ошибку: ${e.message}")
                Toast.makeText(this@MainActivity, "Ошибка сервера: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showResultDialog(processedText: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Результат распознавания")

        if (processedText.isEmpty()) {
            builder.setMessage("Нейросеть не смогла разобрать текст на изображении.")
        } else {
            builder.setMessage(processedText)
        }

        builder.setPositiveButton("Отлично") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
