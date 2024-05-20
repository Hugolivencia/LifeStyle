package com.example.lifestyle.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.lifestyle.Activities.login.ActivityConf
import com.example.lifestyle.Activities.login.LoginActivity
import com.example.lifestyle.Activities.login.ProvaiderType
import com.example.lifestyle.R
import com.example.lifestyle.databinding.ActivityMainBinding
import com.example.lifestyle.fragment.FavoriteFragment
import com.example.lifestyle.fragment.RecipeFragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var navegation : BottomNavigationView
    private lateinit var binding: ActivityMainBinding

    private val mONavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.recipesFragment -> {
                supportFragmentManager.commit {
                    replace<RecipeFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.Favorite -> {
                supportFragmentManager.commit {
                    replace<FavoriteFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.createRecipeFragment -> {
                // Abrir la actividad ActivityMain2
                val intent = Intent(this, ActivityMain2::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }

            R.id.userActivity -> {
                // Abrir la actividad ActivityUser
                val intent = Intent(this, ActivityUser::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }

            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        //setup(email?:"", provider?:"")
        Log.d("Usuario y provaider","gmail: ${email}, provider: ${provider}")
        // Guardar datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()

        // Navegación entre fragment
        navegation = findViewById(R.id.bottomNavigationView)
        navegation.setOnNavigationItemSelectedListener(mONavMenu)

        supportFragmentManager.commit {
            replace<RecipeFragment>(R.id.frameContainer)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }

    }

    /**private fun setup(email:String, provider:String){
        val gmail = findViewById<TextView>(R.id.provaiderUser)
        title="Inicio"
        //gmail.text = email

        val logOut = findViewById<Button>(R.id.btnLogOut)

    }**/
}



