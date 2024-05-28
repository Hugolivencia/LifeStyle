package com.example.lifestyle.fragment.FragmentUser

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifestyle.R
import com.example.lifestyle.RecyclerView.PostAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lifestyle.Activities.ActivityInfo
import com.example.lifestyle.Activities.login.ActivityRegister
import com.example.lifestyle.Recipe2
import com.example.lifestyle.fragment.RecipeFragment
import com.google.firebase.auth.FirebaseAuth


class PostFragment : Fragment() {


    private val recipeMutableList = mutableListOf<Recipe2>()
    val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: PostAdapter
    var img: String? = null
    private lateinit var layout: ViewGroup
    var nombre: String? = null
    var activityRegister = ActivityRegister()
    var username = activityRegister.username



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view =  inflater.inflate(R.layout.fragment_post, container, false)
        val viewUser =  inflater.inflate(R.layout.activity_user, container, false)

        recipeMutableList.clear()
        //recuperarRecetasUsuario()
        db.collection("recipes")
            .whereEqualTo("username", recuperarUsuario())
            .get()
            .addOnSuccessListener { recipe ->
            for(document in recipe.documents){
                val id = document.getString("id")
                nombre = document.getString("nombre")
                val tiempo = document.getString("tiempo")
                val calories = document.getString("calories")

                val ingredients = document.get("ingredientes") as List<String>?
                val ingredientsList: List<String> = ingredients?: listOf()// Si es nulo, asigna una lista vacía

                val descripcion = document.getString("descripcion")

                val img = document.getString("img")

                val pasosPreparacion = document.get("pasosPreparacion") as List<String>?
                val pasosList: List<String> = pasosPreparacion?: listOf()

                // Crear un objeto Recipe con los datos recuperados
                val recipe = Recipe2(id, username ,nombre, tiempo, calories, ingredientsList, descripcion , img , pasosList)
                recipeMutableList.add(recipe)

            }

            adapter.notifyDataSetChanged()
            //Toast.makeText(requireContext(), "La URL de la imagen : $img", Toast.LENGTH_SHORT).show()
            //Toast.makeText(requireContext(), "La lista contiene : $recipe", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            // Manejar el error si la recuperación de datos falla
            Toast.makeText(requireContext(), "Error al recuperar las recetas: ${exception.message}", Toast.LENGTH_SHORT).show()
        }

        initRecyclerView(view)



        return view
        }


    fun recuperarRecetasUsuario(){
        val db = FirebaseFirestore.getInstance()
        val listaRecetasNombre = mutableListOf<Recipe2>()
        db.collection("recipes")
            .whereEqualTo("username", recuperarUsuario())
            .get()
            .addOnSuccessListener { documents ->

                for(document in documents.documents){
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
                        pasosList
                    )

                    listaRecetasNombre.add(recetaUsuario)

                    Log.d("Pruebas", "Lista de receta por usuario:${listaRecetasNombre}")
                    Log.d("Pruebas", "Nombre de usaurio:${recuperarUsuario()}")
                }
                adapter.notifyDataSetChanged()
            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al obtener la receta", exception)
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

    private fun initRecyclerView(view : View) {
        adapter = PostAdapter(recipeMutableList) { recipe ->
            onItemSelected(recipe)
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView2)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = adapter
    }

    fun onItemSelected(recipe: Recipe2) {
        RecipeFragment.RecipeIdHolder.recipeId = recipe.id
        Toast.makeText(requireContext(), "${RecipeFragment.RecipeIdHolder.recipeId}", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), ActivityInfo::class.java)
        //intent.putExtra("clave", valor)
        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()

        // Obtener la actividad que contiene el fragmento
        val activity = requireActivity()

        // Bloquear la acción de retroceso
        activity.onBackPressedDispatcher.addCallback(this) {

        }
    }



    object RecipeIdHolder {
        var recipeId: String? = null
    }

    }