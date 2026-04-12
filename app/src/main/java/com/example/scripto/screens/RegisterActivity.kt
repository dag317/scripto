package com.example.scripto.screens

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.scripto.R
import com.example.scripto.database.ApiResponse
import com.example.scripto.database.RegisterRequest
import com.example.scripto.database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userEmail: EditText = findViewById(R.id.userEmailReg)
        val userPassword: EditText = findViewById(R.id.userPasswordReg)
        val registerButton: Button = findViewById(R.id.buttonRegister)
        val linkToAuth: TextView = findViewById(R.id.linkToAuth)

        registerButton.setOnClickListener {
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

            if (password.length < 6) {
                Toast.makeText(this, "Пароль минимум 6 символов", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            RetrofitClient.api.register(RegisterRequest(email, password)).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {

                        Toast.makeText(this@RegisterActivity, "Пользователь создан", Toast.LENGTH_SHORT).show()
                        userEmail.text.clear()
                        userPassword.text.clear()
                        startActivity(Intent(this@RegisterActivity, AuthActivity::class.java))
                    } else {
                        Toast.makeText(this@RegisterActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        linkToAuth.setOnClickListener {
            linkToAuth.isEnabled = false
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }


    }
}
