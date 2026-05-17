package com.example.scripto.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    private var photoFile: File? = null
    private var photoUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoFile?.let { file ->
                if (file.exists()) {
                    Toast.makeText(this, "Изображение получено. Отправка на сервер...", Toast.LENGTH_SHORT).show()
                    sendImageToOcr(file)
                }
            }
        } else {
            Toast.makeText(this, "Снимок отменен", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            Toast.makeText(this, "Изображение выбрано. Отправка на сервер...", Toast.LENGTH_SHORT).show()
            try {
                val file = File(cacheDir, "gallery_image.jpg")
                contentResolver.openInputStream(selectedUri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (file.exists()) {
                    sendImageToOcr(file)
                }
            } catch (e: Exception) {
                Log.e("GALLERY_ERR", "Ошибка копирования файла: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("ru"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS_ERR", "Русский язык не поддерживается")
                }
            } else {
                Log.e("TTS_ERR", "Ошибка инициализации TextToSpeech")
            }
        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val scanBtn = findViewById<Button>(R.id.scanBtn)

        val homeContainer = findViewById<LinearLayout>(R.id.homeContainer)
        val archiveContainer = findViewById<FrameLayout>(R.id.archiveContainer)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val recyclerView = findViewById<RecyclerView>(R.id.archiveRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ArchiveAdapter(emptyList(), onDeleteClick = {}, onItemClick = {}, onSpeakClick = {})

        logoutBtn.setOnClickListener {
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit { clear() }
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        scanBtn.setOnClickListener {
            val options = arrayOf("Сделать снимок на камеру", "Выбрать готовое из галереи")
            val builder = androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Откуда взять документ?")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> pickImageLauncher.launch("image/*")
                }
                dialog.dismiss()
            }
            builder.show()
        }

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
                    loadArchiveTexts()
                    true
                }
                else -> false
            }
        }
    }

    private fun openCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = cacheDir

            photoFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)

            photoFile?.let { file ->
                photoUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                photoUri?.let { uri ->
                    takePictureLauncher.launch(uri)
                }
            }
        } catch (e: Exception) {
            Log.e("CAMERA_ERR", "Ошибка при создании файла камеры: ${e.message}")
            Toast.makeText(this, "Не удалось запустить камеру", Toast.LENGTH_SHORT).show()
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

    private fun saveTextToDatabase(title: String, content: String) {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "Ошибка авторизации. Войдите заново", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createText("Bearer $token", TextRequest(title, content))
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

    private fun setupRecyclerView(list: List<UserText>) {
        val recyclerView = findViewById<RecyclerView>(R.id.archiveRecyclerView)
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        recyclerView.adapter = ArchiveAdapter(
            list,
            onDeleteClick = { selectedText ->
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.api.deleteText("Bearer $token", selectedText.id)
                        if (response.isSuccessful) {

                            if (tts?.isSpeaking == true) {
                                tts?.stop()
                            }

                            loadArchiveTexts()
                            Toast.makeText(this@MainActivity, "Успешно удалено", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Не удалось удалить", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onItemClick = { selectedText ->
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setTitle(selectedText.title)
                builder.setMessage(selectedText.content)
                builder.setPositiveButton("Закрыть") { d, _ -> d.dismiss() }
                builder.show()
            },
            onSpeakClick = { selectedText ->
                speakText(selectedText.content)
            }
        )
    }

    private fun speakText(text: String) {
        if (tts != null) {
            if (tts!!.isSpeaking) {
                tts?.stop()
                Toast.makeText(this, "Озовучка остановлена", Toast.LENGTH_SHORT).show()
            } else {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }
}
