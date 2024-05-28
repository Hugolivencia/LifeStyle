package com.example.lifestyle.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.lifestyle.Activities.login.ActivityConf
import com.example.lifestyle.Activities.login.ActivityRegister
import com.example.lifestyle.Activities.login.LoginActivity
import com.example.lifestyle.Activities.login.ProvaiderType
import com.example.lifestyle.R
import com.example.lifestyle.Recipe2
import com.example.lifestyle.databinding.ActivityUserBinding
import com.example.lifestyle.fragment.FragmentUser.PostFragment
import com.example.lifestyle.fragment.FragmentUser.RecetasGuardadasFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityUser : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()
    lateinit var navegation : BottomNavigationView
    private lateinit var binding: ActivityUserBinding
    var activityRegister = ActivityRegister()
    var username = activityRegister.username

    private val mONavMenuUser = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.menuFragment -> {
                supportFragmentManager.commit {
                    replace<PostFragment>(R.id.frameContainerUser)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.menuSave -> {
                supportFragmentManager.commit {
                    replace<RecetasGuardadasFragment>(R.id.frameContainerUser)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usuarioActual = auth.currentUser
        if(usuarioActual != null){
            // Obtener el nombre de usuario del usuario autentificado por firebase
            val nombreUsuario = usuarioActual.displayName

            // Mostrar el nombre de usuario en un TextView
            val textView = findViewById<TextView>(R.id.txtNomUsuario)
            textView.text = "$nombreUsuario"

        }

        // Método para recuperar las recetas de un usuario y almacenarlas en una lista
        recuperarRecetasUsuario()


        binding.btnConfiguration.setOnClickListener {
            val email = FirebaseAuth.getInstance().currentUser?.email
            showHome2(email!!, ProvaiderType.BASIC)

        }

        binding.arrowBack.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        // Navegación entre fragment
        navegation = findViewById(R.id.bottomNavigationViewUser)
        navegation.setOnNavigationItemSelectedListener(mONavMenuUser)

        supportFragmentManager.commit {
            replace<PostFragment>(R.id.frameContainerUser)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }
    }




    fun recuperarRecetasUsuario(){
        val db = FirebaseFirestore.getInstance()
        val listaRecetaUsuario = mutableListOf<Recipe2>()
        db.collection("recipes")
            .whereEqualTo("username", recuperarUsuario())
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val id = document.getString("id")
                    val nombre = document.getString("nombre")
                    val tiempo = document.getString("tiempo")
                    val calories = document.getString("calories")

                    val ingredients = document.get("ingredientes") as List<String>?
                    val ingredientsList: List<String> = ingredients?: listOf()// Si es nulo, asigna una lista vacía

                    val descripcion = document.getString("descripcion")

                    val img = document.getString("img")

                    val pasosPreparacion = document.get("pasosPreparacion") as List<String>?
                    val pasosList: List<String> = pasosPreparacion?: listOf()

                    // Crear un objeto de receta
                    val recetaUsuario = Recipe2(
                        id,
                        username,
                        nombre,
                        tiempo,
                        calories,
                        ingredientsList,
                        descripcion,
                        img,
                        pasosList)

                    listaRecetaUsuario.add(recetaUsuario)
                    Log.d("Pruebas", "Lista de receta por usuario:${listaRecetaUsuario}")
                    Log.d("Pruebas", "Nombre de usaurio:${recuperarUsuario()}")
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error al obtener la receta", exception)
            }



    }
    private fun recuperarUsuario(): String? {
        val auth = FirebaseAuth.getInstance()
        val usuarioActual = auth.currentUser
        var nombreUsuario: String? = null  // Inicializar la variable fuera del bloque if

        if (usuarioActual != null) {
            // Obtener el nombre de usuario del usuario autentificado por Firebase
            nombreUsuario = usuarioActual.displayName
        }

        Log.d("ActivityRegister", "UsuarioActivity10: $nombreUsuario")
        return nombreUsuario
    }

    private fun showHome2(usuario: String, provider: ProvaiderType) {
        val intent = Intent(this, ActivityConf::class.java).apply {
            putExtra("email", usuario)
            putExtra("provider", provider.name)
        }
        startActivity(intent)
    }
}