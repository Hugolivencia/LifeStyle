package com.example.lifestyle.Activities.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.lifestyle.R
import com.example.lifestyle.fragment.RecipeFragment.Companion.provaider
import com.example.lifestyle.fragment.RecipeFragment.Companion.usuario
import com.google.firebase.auth.FirebaseAuth



class ActivityConf : AppCompatActivity() {
    /**companion object {
        var usuario: String = ""
        var provaider: ProvaiderType? = null
    }**/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conf)


        val user = findViewById<TextView>(R.id.provaiderUser)


        // Recuperar los datos
        user.text = usuario

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email?:"", provider?:"")


        // Guardado de datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }


    private fun setup(email:String, provider:String) {
        val gmail = findViewById<TextView>(R.id.provaiderUser)
        title="Inicio"
        gmail.text = email

        val logOut = findViewById<Button>(R.id.btnLogOut)

        logOut.setOnClickListener {
            // Borrar datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        }

    }
