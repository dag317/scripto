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
import com.example.scripto.utils.OtpSession
import kotlin.jvm.java

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val sendBtn = findViewById<Button>(R.id.sendOtpBtn)
        val cancelReset = findViewById<TextView>(R.id.cancelForgot)

        cancelReset.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        sendBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ⚠️ SECURITY: всегда одинаковый ответ
            val otp = (100000..999999).random().toString()

            OtpSession.email = email
            OtpSession.otp = otp
            OtpSession.expireTime = System.currentTimeMillis() + 5 * 60 * 1000

            // simulate sending email
            sendFakeEmail(email, otp)

            startActivity(Intent(this, OtpActivity::class.java))
        }
    }

    private fun sendFakeEmail(email: String, otp: String) {
        // backend usually does this
        println("Sending OTP $otp to $email")
    }
}