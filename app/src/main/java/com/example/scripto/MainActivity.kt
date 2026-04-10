package com.example.scripto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userEmail: EditText = findViewById(R.id.userEmailReg)
        val userPassword: EditText = findViewById(R.id.userPasswordReg)
        val registerButton: Button = findViewById(R.id.buttonRegister)
        val linkToAuth: TextView = findViewById(R.id.linkToAuth)

        registerButton.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (email == "" || password == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {
                val user = User(email, password)

                val db = DbHelper(this, null)
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
