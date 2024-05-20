package com.example.lifestyle.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.lifestyle.Activities.login.LoginActivity
import com.example.lifestyle.R

class PantallaCarga : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_carga)


        screenSplash.setKeepOnScreenCondition{ true }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

    }
}