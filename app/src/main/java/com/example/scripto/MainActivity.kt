package com.example.scripto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userEmail: EditText = findViewById(R.id.userEmailLogin)
        val userPassword: EditText = findViewById(R.id.userPasswordLogin)
        val registerButton: Button = findViewById(R.id.buttonRegister)

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
    }
}
