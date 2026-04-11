package com.example.scripto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.utils.HashUtils.hashPassword

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

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Неверный email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val db = DbHelper(this, null)

            val hashedPassword = hashPassword(password)
            val isAuth = db.getUser(email, hashedPassword)

            if (isAuth) {
                Toast.makeText(this, "Успешный вход", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_LONG).show()
            }
        }

        linkToReg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}