package com.example.scripto.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.R
import com.example.scripto.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Patterns
import android.widget.LinearLayout
import android.os.Handler
import android.os.Looper

    class ForgotPasswordActivity : AppCompatActivity() {
        private lateinit var dot1: TextView
        private lateinit var dot2: TextView
        private lateinit var dot3: TextView
        private lateinit var loadingDotsContainer: LinearLayout

        private var animationRunning = false  // Флаг для отслеживания анимации

        private var isEmailEntered = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_forgot_password)

            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            setupViews()
        }

        private fun setupViews() {
            val emailInput = findViewById<EditText>(R.id.emailInput)
            val codeContainer = findViewById<LinearLayout>(R.id.codeContainer)
            val otpInput = findViewById<EditText>(R.id.otpInput)
            val sendBtn = findViewById<Button>(R.id.sendOtpBtn)
            val verifyBtn = findViewById<Button>(R.id.verifyBtn)
            val resendBtn = findViewById<TextView>(R.id.resendButton)
            val cancelReset = findViewById<TextView>(R.id.cancelForgot)
            val frBu = findViewById<Button>(R.id.frozenButton)
            loadingDotsContainer = findViewById(R.id.loadingDotsContainer)
            dot1 = findViewById(R.id.dot1)
            dot2 = findViewById(R.id.dot2)
            dot3 = findViewById(R.id.dot3)

            loadingDotsContainer.visibility = View.GONE
            // Изначально скрываем контейнер с OTP
            codeContainer.visibility = View.GONE
            frBu.visibility = View.GONE
            sendBtn.visibility = View.VISIBLE
            cancelReset.setOnClickListener {
                cancelReset.isEnabled = false
                val intent = Intent(this, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            sendBtn.setOnClickListener {
                if (!isEmailEntered) {
                    // Этап 1: отправка кода по email
                    val email = emailInput.text.toString().trim()

                    if (email.isEmpty()) {
                        Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (!isValidEmail(email)) {
                        emailInput.error = "Введите корректный email"
                        return@setOnClickListener
                    }

                    // Показываем анимацию загрузки
                    showLoadingAnimation()
                    sendBtn.visibility = View.GONE
                    frBu.visibility = View.VISIBLE

                    OtpSession.email = email
                    RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email))
                        .enqueue(object : Callback<ApiResponse> {
                            override fun onResponse(
                                call: Call<ApiResponse>,
                                response: Response<ApiResponse>
                            ) {
                                // Скрываем анимацию после получения ответа
                                hideLoadingAnimation()

                                if (response.isSuccessful) {
                                    // Переключаем интерфейс на этап ввода OTP
                                    isEmailEntered = true
                                    frBu.visibility = View.GONE  // Скрываем кнопку отправки email
                                    codeContainer.visibility = View.VISIBLE
                                    verifyBtn.visibility = View.VISIBLE  // Показываем кнопку подтверждения кода

                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Код отправлен",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startResendTimer(resendBtn)
                                } else {
                                    val errorBody = response.errorBody()?.string() ?: ""

                                    if (response.code() == 404) {
                                        emailInput.error = "Пользователь с такой почтой не найден"
                                    } else {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "Ошибка: $errorBody",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Ошибка сети: ${t.message}, попробуйте позже",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            }

            verifyBtn.setOnClickListener {
// Этап 2: проверка OTP кода
                val code = otpInput.text.toString().trim()

                if (code.isEmpty()) {
                    Toast.makeText(this, "Введите код", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                RetrofitClient.api.verifyOtp(VerifyOtpRequest(OtpSession.email, code))
                    .enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(
                            call: Call<ApiResponse>,
                            response: Response<ApiResponse>
                        ) {
                            val body = response.body()

                            if (body?.success == true) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Код верный",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    body?.error ?: "Ошибка",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            hideLoadingAnimation()
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Ошибка сети",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }

            resendBtn.setOnClickListener {
                resendOtp(OtpSession.email, resendBtn)
            }
        }

        // Вспомогательная функция для проверки email
        private fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        // Функция для повторной отправки OTP
        private fun resendOtp(email: String?, resendButton: TextView) {
            RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email))
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Код отправлен повторно",
                                Toast.LENGTH_SHORT
                            ).show()
                            startResendTimer(resendButton)
                        } else {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Ошибка сервера",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Ошибка сети: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        // Таймер для кнопки повторной отправки
        @SuppressLint("SetTextI18n")
        private fun startResendTimer(resendButton: TextView) {
            resendButton.isEnabled = false
            resendButton.setTextColor(Color.GRAY)

            object : android.os.CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    resendButton.text = "Отправить снова через ${millisUntilFinished / 1000}с"
                }

                override fun onFinish() {
                    resendButton.isEnabled = true
                    resendButton.text = "Отправить снова"
                    resendButton.setTextColor(Color.BLUE)
                }
            }.start()
        }
        private fun showLoadingAnimation() {
            loadingDotsContainer.visibility = View.VISIBLE
            startSimpleDotsAnimation()
        }

        // Скрытие анимации загрузки
        private fun hideLoadingAnimation() {
            loadingDotsContainer.visibility = View.GONE
            stopSimpleDotsAnimation()
        }

        // Запуск простой анимации мигания
        private fun startSimpleDotsAnimation() {
            if (animationRunning) return
            animationRunning = true

            val handler = Handler(Looper.getMainLooper())
            var currentDot = 0

            val runnable = object : Runnable {
                override fun run() {
                    // Сначала скрываем все точки
                    dot1.alpha = 0f
                    dot2.alpha = 0f
                    dot3.alpha = 0f

                    // Показываем текущую точку
                    when (currentDot) {
                        0 -> dot1.alpha = 1f
                        1 -> dot2.alpha = 1f
                        2 -> dot3.alpha = 1f
                    }

                    // Переходим к следующей точке
                    currentDot = (currentDot + 1) % 3

                    // Запускаем следующий кадр через 300 мс
                    handler.postDelayed(this, 300)
                }
            }

            // Сохраняем ссылку на runnable для остановки
            handler.post(runnable)
        }

        // Остановка анимации
        private fun stopSimpleDotsAnimation() {
            animationRunning = false
        }
    }