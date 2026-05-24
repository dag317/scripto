package com.example.scripto.screens

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.scripto.R
import com.example.scripto.database.LoginRequest
import com.example.scripto.database.LoginResponse
import com.example.scripto.database.RetrofitClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {

    private fun sendGoogleTokenToBackend(idToken: String) {
        val requestBody = mapOf("idToken" to idToken)

        RetrofitClient.api.googleLogin(requestBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    getSharedPreferences("auth", MODE_PRIVATE).edit {
                        putString("token", token)
                    }
                    Toast.makeText(
                        this@AuthActivity,
                        "Вход через Google успешен",
                        Toast.LENGTH_SHORT
                    ).show()

                    // УБРАЛИ finish(), НО ДОБАВИЛИ ФЛАГИ
                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@AuthActivity,
                        "Ошибка сервера при входе Google",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@AuthActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val userEmail: EditText = findViewById(R.id.userEmailLogin)
        val userPassword: EditText = findViewById(R.id.userPasswordLogin)
        val authButton: Button = findViewById(R.id.buttonLogin)
        val linkToReg: TextView = findViewById(R.id.linkToReg)
        val btnGoogle: Button = findViewById(R.id.btnGoogleAuth)
        val forgotPassword: TextView = findViewById(R.id.forgotPassword)
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("365323608098-a9du3snf98ovv5eqbmpspivo9l4un0vh.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account?.idToken

                    if (idToken != null) {
                        sendGoogleTokenToBackend(idToken)
                    } else {
                        Log.e("GOOGLE_DEBUG", "idToken is NULL")
                        Toast.makeText(this, "Google не выдал токен", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: ApiException) {
                    val statusCode = e.statusCode
                    val message = e.message

                    Log.e("GOOGLE_DEBUG", "Код ошибки: $statusCode")
                    Log.e("GOOGLE_DEBUG", "Сообщение: $message")
                    e.printStackTrace()

                    Toast.makeText(this, "Ошибка Google ($statusCode): $message", Toast.LENGTH_LONG)
                        .show()
                }
            }

// ИЗМЕНЁН БЛОК ПРОВЕРКИ ТОКЕНА С ФЛАГАМИ
        if (token != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        authButton.setOnClickListener {
            authButton.isEnabled = false // Блокируем кнопку на время запроса

            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
                authButton.isEnabled = true // Включаем обратно при ошибке
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Неверный email", Toast.LENGTH_LONG).show()
                authButton.isEnabled = true
                return@setOnClickListener
            }

            RetrofitClient.api.login(LoginRequest(email, password))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        authButton.isEnabled = true // Всегда включаем кнопку после ответа

                        if (response.isSuccessful) {
                            Toast.makeText(this@AuthActivity, "Успешный вход", Toast.LENGTH_SHORT)
                                .show()
                            val token = response.body()?.token

                            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                            prefs.edit {
                                putString(
                                    "token",
                                    token
                                )
                            }

                            // УБРАЛИ finish(), ДОБАВИЛИ ФЛАГИ ДЛЯ КОРРЕКТНОГО СТЕКА
                            val intent = Intent(this@AuthActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                        } else {
                            // Обработка ошибок
                            val errorText = response.errorBody()?.string() ?: ""

                            if (errorText.contains("Please verify your email first")) {
                                val builder = android.app.AlertDialog.Builder(this@AuthActivity)
                                    .setTitle("Почта не подтверждена")
                                    .setMessage("Мы отправили ссылку на ваш email. Пожалуйста, подтвердите его перед входом.")
                                    .setPositiveButton("ОК", null)

                                val dialog = builder.create()
                                dialog.show()

                                // Сразу после показа диалога меняем цвет текста кнопки
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                                    ContextCompat.getColor(this@AuthActivity, android.R.color.black)
                                )
                            } else {
                                Toast.makeText(
                                    this@AuthActivity,
                                    "Неверный email или пароль",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        authButton.isEnabled = true // Включаем кнопку при ошибке сети
                        Toast.makeText(
                            this@AuthActivity,
                            "Ошибка сети: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
        linkToReg.setOnClickListener {
            linkToReg.isEnabled = false
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            linkToReg.isEnabled = true // Включаем кнопку после перехода
        }

        forgotPassword.setOnClickListener {
            forgotPassword.isEnabled = false
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            forgotPassword.isEnabled = true // Включаем кнопку после перехода
        }

        btnGoogle.setOnClickListener {
            btnGoogle.isEnabled = false // Блокируем кнопку на время выполнения
            val signInIntent = googleSignInClient.signInIntent
            googleLauncher.launch(signInIntent)
            // Кнопка будет разблокирована в callback'е googleLauncher
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Этот метод оставляем на случай, если будут другие ActivityResult
    }

    // Добавляем обработку кнопки «Назад» для корректного выхода
    override fun onDestroy() {
        super.onDestroy()
        // Освобождаем ресурсы, если нужно
    }
}