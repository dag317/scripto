package com.example.scripto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.utils.OtpSession

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val pass = findViewById<EditText>(R.id.newPassword)
        val confirm = findViewById<EditText>(R.id.confirmPassword)
        val save = findViewById<Button>(R.id.saveBtn)
        val cancelReset = findViewById<TextView>(R.id.cancelReset)

        cancelReset.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        save.setOnClickListener {
            val p1 = pass.text.toString()
            val p2 = confirm.text.toString()

            if (p1.length < 8) {
                Toast.makeText(this, "Минимум 8 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (p1 != p2) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // simulate save
            savePassword(OtpSession.email, p1)

            Toast.makeText(this, "Пароль обновлён", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun savePassword(email: String, password: String) {
        println("Password updated for $email")
    }
}