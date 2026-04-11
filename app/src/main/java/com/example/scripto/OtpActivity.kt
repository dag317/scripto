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

class OtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val otpInput = findViewById<EditText>(R.id.otpInput)
        val verifyBtn = findViewById<Button>(R.id.verifyBtn)
        val cancelOtp = findViewById<TextView>(R.id.cancelOtp)

        cancelOtp.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        verifyBtn.setOnClickListener {
            val code = otpInput.text.toString()

            if (System.currentTimeMillis() > OtpSession.expireTime) {
                Toast.makeText(this, "Код истёк", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (code == OtpSession.otp) {
                startActivity(Intent(this, ResetPasswordActivity::class.java))
            } else {
                OtpSession.attempts++

                if (OtpSession.attempts >= 5) {
                    Toast.makeText(this, "Слишком много попыток", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}