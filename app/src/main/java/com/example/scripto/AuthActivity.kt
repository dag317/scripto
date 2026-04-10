package com.example.scripto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userEmail: EditText = findViewById(R.id.userEmailLogin)
        val userPassword: EditText = findViewById(R.id.userPasswordLogin)
        val authButton: Button = findViewById(R.id.buttonLogin)
        val linkToReg: TextView = findViewById(R.id.linkToReg)

        authButton.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (email == "" || password == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {
                val db = DbHelper(this, null)
                val isAuth = db.getUser(email, password)

                if (isAuth) {
                    Toast.makeText(this, "Пользователь $email авторизован", Toast.LENGTH_LONG).show()
                    userEmail.text.clear()
                    userPassword.text.clear()
                } else
                    Toast.makeText(this, "Пользователь $email НЕ авторизован", Toast.LENGTH_LONG).show()

            }
        }

        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}