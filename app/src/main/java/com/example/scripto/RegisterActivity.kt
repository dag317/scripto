package com.example.scripto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.scripto.utils.HashUtils.hashPassword

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

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Неверный email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Пароль минимум 6 символов", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val db = DbHelper(this, null)

            if (db.isUserExists(email)) {
                Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_LONG).show()
            } else {
                val hashedPassword = hashPassword(password)
                val user = User(email, hashedPassword)

                db.addUser(user)
                Toast.makeText(this, "Пользователь $email добавлен", Toast.LENGTH_LONG).show()

                userEmail.text.clear()
                userPassword.text.clear()
            }
        }

        linkToAuth.setOnClickListener {
            try {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("MY_ERROR", "Ошибка перехода: ${e.message}", e)
            }
        }


    }
}
