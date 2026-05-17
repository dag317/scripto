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

        // Логика выхода
        logoutBtn.setOnClickListener {
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit { clear() }
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        // Логика тестирования OCR
        scanBtn.setOnClickListener {
            Toast.makeText(this, "Отправка на сервер...", Toast.LENGTH_SHORT).show()

            // 1. Берем фотку из папки assets
            val testFile = getFileFromAssets("test_notes.jpg")

            if (testFile != null && testFile.exists()) {
                // 2. Стреляем в сервер
                sendImageToOcr(testFile)
            } else {
                Toast.makeText(this, "Файл test_notes.jpg не найден в assets!", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Вспомогательная функция: копирует файл из папки assets во внутреннюю память, чтобы передать в Retrofit
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

    // Отправка файла на Node.js сервер
    private fun sendImageToOcr(imageFile: File) {
        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

        lifecycleScope.launch {
            try {
                // Вызываем твой рабочий Retrofit клиент
                val response = RetrofitClient.api.uploadOcrImage(body)

                // Смотрим результат в консоли Android Studio (Logcat)
                Log.d("OCR_TEST_RESULT", "=== СЫРОЙ ТЕКСТ ===")
                Log.d("OCR_TEST_RESULT", response.raw_text)

                Log.d("OCR_TEST_RESULT", "=== ИСПРАВЛЕННЫЙ ТЕКСТ ===")
                Log.d("OCR_TEST_RESULT", response.corrected_text)

                // Выводим юзеру тост-уведомление
                Toast.makeText(this@MainActivity, "Распознано! Ищи текст в Logcat по тегу OCR_TEST_RESULT", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Log.e("OCR_TEST_ERROR", "Сервер вернул ошибку: ${e.message}")
                Toast.makeText(this@MainActivity, "Ошибка сервера: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
