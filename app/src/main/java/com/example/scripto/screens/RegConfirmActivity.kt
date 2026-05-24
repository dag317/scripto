package com.example.scripto.screens

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.scripto.R

@Suppress("DEPRECATION")
class RegConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg_confirm)
        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val toLoginButton = findViewById<Button>(R.id.toLogin)

        toLoginButton.setOnClickListener {
            toLoginButton.isEnabled = false
            startActivity(Intent(this@RegConfirmActivity, AuthActivity::class.java))
            finish()
        }
    }
}