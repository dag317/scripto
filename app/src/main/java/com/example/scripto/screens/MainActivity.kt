package com.example.scripto.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scripto.R
import com.example.scripto.database.RetrofitClient
import com.example.scripto.database.TextRequest
import com.example.scripto.database.UserText
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Скрываем фиолетовый Toolbar сверху для чистого белого дизайна
        supportActionBar?.hide()

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val scanBtn = findViewById<Button>(R.id.scanBtn)

        // Элементы переключения экранов
        val homeContainer = findViewById<LinearLayout>(R.id.homeContainer)
        val archiveContainer = findViewById<FrameLayout>(R.id.archiveContainer)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

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

        // Логика переключения вкладок в нижнем меню
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    homeContainer.visibility = View.VISIBLE
                    archiveContainer.visibility = View.GONE
                    true
                }
                R.id.nav_archive -> {
                    homeContainer.visibility = View.GONE
                    archiveContainer.visibility = View.VISIBLE
                    loadArchiveTexts() // Загружаем список из базы данных сервера
                    true
                }
                else -> false
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
            builder.setPositiveButton("Отлично") { dialog, _ -> dialog.dismiss() }
        } else {
            builder.setMessage(processedText)

            // Новая кнопка сохранения текста в базу данных
            builder.setNegativeButton("В архив") { dialog, _ ->
                val currentDate = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date())
                saveTextToDatabase("Скан от $currentDate", processedText)
                dialog.dismiss()
            }
            builder.setPositiveButton("Просто закрыть") { dialog, _ -> dialog.dismiss() }
        }

        val dialog = builder.create()
        dialog.show()
    }

    // Отправка распознанного текста в базу данных MySQL через Node.js
    private fun saveTextToDatabase(title: String, content: String) {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "Ошибка авторизации. Войдите заново", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createText("Bearer $token",
                    TextRequest(title, content)
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Сохранено в архив!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Не удалось сохранить", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Загрузка всех текстов пользователя с сервера
    private fun loadArchiveTexts() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getTexts("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    setupRecyclerView(response.body()!!)
                } else {
                    Toast.makeText(this@MainActivity, "Ошибка загрузки архива", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Настройка списка отображения (RecyclerView) и обработка удаления
    private fun setupRecyclerView(list: List<UserText>) {
        val recyclerView = findViewById<RecyclerView>(R.id.archiveRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        recyclerView.adapter = ArchiveAdapter(
            list,
            onDeleteClick = { selectedText ->
                // Отправляем DELETE запрос на сервер
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.api.deleteText("Bearer $token", selectedText.id)
                        if (response.isSuccessful) {
                            loadArchiveTexts() // Перезагружаем список после удаления
                            Toast.makeText(this@MainActivity, "Успешно удалено", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Не удалось удалить", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onItemClick = { selectedText ->
                // Просмотр полного текста при клике на карточку архива
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setTitle(selectedText.title)
                builder.setMessage(selectedText.content)
                builder.setPositiveButton("Закрыть") { d, _ -> d.dismiss() }
                builder.show()
            }
        )
    }
}
