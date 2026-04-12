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
import com.example.scripto.database.ResetPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val pass = findViewById<EditText>(R.id.newPassword)
        val confirm = findViewById<EditText>(R.id.confirmPassword)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        val cancelReset = findViewById<TextView>(R.id.cancelReset)

        cancelReset.setOnClickListener {
            cancelReset.isEnabled = false
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            val p1 = pass.text.toString().trim()
            val p2 = confirm.text.toString().trim()
            val email = intent.getStringExtra("email") ?: OtpSession.email ?: ""

            if (p1.length < 6) {
                Toast.makeText(this, "Минимум 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (p1 != p2) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.api.resetPassword(ResetPasswordRequest(email, p1)).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ResetPasswordActivity, "Пароль обновлён", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ResetPasswordActivity, AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ResetPasswordActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@ResetPasswordActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}