package com.example.scripto.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.scripto.R

class RegConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg_confirm)

        val toLoginButton = findViewById<Button>(R.id.toLogin)

        toLoginButton.setOnClickListener {
            toLoginButton.isEnabled = false
            startActivity(Intent(this@RegConfirmActivity, AuthActivity::class.java))
            finish()
        }
    }
}