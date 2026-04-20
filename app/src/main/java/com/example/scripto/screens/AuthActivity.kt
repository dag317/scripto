package com.example.scripto.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.R
import com.example.scripto.database.LoginRequest
import com.example.scripto.database.LoginResponse
import com.example.scripto.database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class AuthActivity : AppCompatActivity() {
    private fun sendGoogleTokenToBackend(idToken: String) {
        // ВАЖНО: Тебе нужно добавить в ApiService метод googleLogin(body: Map<String, String>)
        val requestBody = mapOf("idToken" to idToken)

        RetrofitClient.api.googleLogin(requestBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    getSharedPreferences("auth", MODE_PRIVATE).edit {
                        putString("token", token)
                    }
                    Toast.makeText(this@AuthActivity, "Вход через Google успешен", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@AuthActivity, "Ошибка сервера при входе Google", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@AuthActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userEmail: EditText = findViewById(R.id.userEmailLogin)
        val userPassword: EditText = findViewById(R.id.userPasswordLogin)
        val authButton: Button = findViewById(R.id.buttonLogin)
        val linkToReg: TextView = findViewById(R.id.linkToReg)
        val btnGoogle: Button = findViewById(R.id.btnGoogleAuth)
        val forgotPassword: TextView = findViewById(R.id.forgotPassword)
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("365323608098-a9du3snf98ovv5eqbmpspivo9l4un0vh.apps.googleusercontent.com").requestEmail().build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken

                if (idToken != null) {
                    sendGoogleTokenToBackend(idToken)
                } else {
                    // Ошибка: токен пустой
                    Log.e("GOOGLE_DEBUG", "idToken is NULL")
                    Toast.makeText(this, "Google не выдал токен", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                // --- ВОТ СЮДА ВСТАВЛЯЕМ ДИАГНОСТИКУ ---
                val statusCode = e.statusCode
                val message = e.message

                // 1. Вывод в консоль (Logcat)
                Log.e("GOOGLE_DEBUG", "Код ошибки: $statusCode")
                Log.e("GOOGLE_DEBUG", "Сообщение: $message")
                e.printStackTrace()

                // 2. Вывод на экран телефона
                Toast.makeText(this, "Ошибка Google ($statusCode): $message", Toast.LENGTH_LONG).show()
            }
        }
        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        authButton.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Неверный email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            RetrofitClient.api.login(LoginRequest(email, password)).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AuthActivity, "Успешный вход", Toast.LENGTH_SHORT).show()
                        val token = response.body()?.token

                        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                        prefs.edit { putString("token", token) } // Используем apply() или edit { ... }

                        val intent = Intent(this@AuthActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // 1. Читаем текст ошибки
                        val errorText = response.errorBody()?.string() ?: ""

                        // 2. Проверяем конкретную фразу, которую кидает бэкенд
                        if (errorText.contains("Please verify your email first")) {
                            android.app.AlertDialog.Builder(this@AuthActivity)
                                .setTitle("Почта не подтверждена")
                                .setMessage("Мы отправили ссылку на ваш email. Пожалуйста, подтвердите его перед входом.")
                                .setPositiveButton("ОК", null)
                                .show()
                        } else {
                            // Если почта подтверждена, но данные неверные
                            Toast.makeText(this@AuthActivity, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@AuthActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        linkToReg.setOnClickListener {
            linkToReg.isEnabled = false
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            forgotPassword.isEnabled = false
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleLauncher.launch(signInIntent)
        }
    }
}