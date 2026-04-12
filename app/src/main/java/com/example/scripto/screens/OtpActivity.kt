package com.example.scripto.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.R
import com.example.scripto.database.ApiResponse
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
        val cancelOtp = findViewById<TextView>(R.id.cancelOtp)

        cancelOtp.setOnClickListener {
            cancelOtp.isEnabled = false
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        verifyBtn.setOnClickListener {
            val code = otpInput.text.toString().trim()
            val email = intent.getStringExtra("email") ?: OtpSession.email ?: ""

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