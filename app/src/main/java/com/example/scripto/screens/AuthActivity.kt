package com.example.scripto.screens

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.R
import com.example.scripto.database.ApiResponse
import com.example.scripto.database.LoginRequest
import com.example.scripto.database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userEmail: EditText = findViewById(R.id.userEmailLogin)
        val userPassword: EditText = findViewById(R.id.userPasswordLogin)
        val authButton: Button = findViewById(R.id.buttonLogin)
        val linkToReg: TextView = findViewById(R.id.linkToReg)
        val forgotPassword: TextView = findViewById(R.id.forgotPassword)

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

            RetrofitClient.api.login(LoginRequest(email, password)).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AuthActivity, "Успешный вход", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AuthActivity, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
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
    }
}