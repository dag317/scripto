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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val sendBtn = findViewById<Button>(R.id.sendOtpBtn)
        val cancelReset = findViewById<TextView>(R.id.cancelForgot)

        cancelReset.setOnClickListener {
            cancelReset.isEnabled = false
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        sendBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            OtpSession.email = email
            RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email)).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Код отправлен", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ForgotPasswordActivity, OtpActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: ""

                        if (response.code() == 404) {
                            emailInput.error = "Пользователь с такой почтой не найден"
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, "Ошибка: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@ForgotPasswordActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}