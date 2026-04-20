package com.example.scripto.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.R
import com.example.scripto.database.ApiResponse
import com.example.scripto.database.ForgotPasswordRequest
import com.example.scripto.database.OtpSession
import com.example.scripto.database.RetrofitClient
import com.example.scripto.database.VerifyOtpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val otpInput = findViewById<EditText>(R.id.otpInput)
        val verifyBtn = findViewById<Button>(R.id.verifyBtn)
        val resendBtn = findViewById<TextView>(R.id.resendButton)
        val cancelOtp = findViewById<TextView>(R.id.cancelOtp)
        val email = intent.getStringExtra("email") ?: OtpSession.email ?: ""

        fun startResendTimer() {
            resendBtn.isEnabled = false // Делаем кнопку некликбельной
            resendBtn.setTextColor(android.graphics.Color.GRAY) // Опционально: серый цвет

            object : android.os.CountDownTimer(60000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    // Отображаем текст с секундами рядом
                    resendBtn.text = "Отправить снова через ${millisUntilFinished / 1000}с"
                }

                override fun onFinish() {
                    resendBtn.isEnabled = true // Включаем обратно
                    resendBtn.text = "Отправить снова"
                    resendBtn.setTextColor(android.graphics.Color.BLUE) // Возвращаем яркий цвет (или твой цвет из XML)
                }
            }.start()
        }

        startResendTimer()

        cancelOtp.setOnClickListener {
            cancelOtp.isEnabled = false
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        resendBtn.setOnClickListener {
            RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email)).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OtpActivity, "Код отправлен", Toast.LENGTH_SHORT).show()
                        startResendTimer()
                    } else {
                        Toast.makeText(this@OtpActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@OtpActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        verifyBtn.setOnClickListener {
            val code = otpInput.text.toString().trim()

            if (code.isEmpty()) {
                Toast.makeText(this, "Введите код", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.api.verifyOtp(VerifyOtpRequest(email, code)).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    val body = response.body()

                    if (body?.success == true) {
                        Toast.makeText(this@OtpActivity, "Код верный", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@OtpActivity, ResetPasswordActivity::class.java))
                    } else {
                        Toast.makeText(this@OtpActivity, body?.error ?: "Ошибка", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@OtpActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}