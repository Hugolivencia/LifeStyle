package com.example.lifestyle.fragment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.lifestyle.Activities.login.ActivityConf
import com.example.lifestyle.Activities.login.ActivityRegister
import com.example.lifestyle.Activities.login.ProvaiderType
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.RecipeFavorite
import com.example.lifestyle.RecyclerView.FavoriteAdapter
import com.example.lifestyle.RecyclerView.RecipeAdapter
import com.example.lifestyle.ViewModel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RecipeFragment : Fragment() {
    private lateinit var adapter: RecipeAdapter
    private val recipeMutableList = mutableListOf<Recipe>()
    private val recipeMutableList2 = mutableListOf<Recipe>()
    val db = FirebaseFirestore.getInstance()
    var activityRegister = ActivityRegister()
    var username = activityRegister.username
    var nombre: String? = null
    var id:String? = null

    companion object {
        var usuario: String = ""
        var provaider: ProvaiderType? = null
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_recipe, container, false)
        //linearLayoutIngredientes = view.findViewById<LinearLayout>(R.id.linearLayoutInfo)


        // Recuperar todas las recetas
        recipeMutableList.clear()
        db.collection("recipes").get()
            .addOnSuccessListener { documents ->
                for (document in documents.documents) {
                    id = document.getString("id")
                    nombre = document.getString("nombre")
                    val tiempo = document.getString("tiempo")
                    val calories = document.getString("calories")

                    val ingredients = document.get("ingredientes") as List<String>?
                    val ingredientsList: List<String> =
                        ingredients ?: listOf()// Si es nulo, asigna una lista vacía

                    val descripcion = document.getString("descripcion")

                    val img = document.getString("img")

                    val pasosPreparacion = document.get("pasosPreparacion") as List<String>?
                    val pasosList: List<String> = pasosPreparacion ?: listOf()

                    // Crear un objeto Recipe con los datos recuperados
                    val recipe = Recipe(
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

                    recipeMutableList.add(recipe)
                    Log.d("liposuccion","la id es: ${id}")
                    Log.d("seccion","el nombre es: ${nombre}")
                }
                // Notificar al adaptador sobre el cambio en los datos
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                // Manejar el error si la recuperación de datos falla
                Toast.makeText(
                    requireContext(),
                    "Error al recuperar las recetas: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        initRecyclerView(view)

        val botonLike = view?.findViewById<LottieAnimationView>(R.id.botonLike)
        botonLike?.setOnClickListener {
            val recipe = Recipe()
            RecipeIdHolder.recipeId = recipe.id
            Toast.makeText(requireContext(), "${RecipeIdHolder.recipeId}", Toast.LENGTH_SHORT).show()
        }

        isRecipeSavedAsFavorite()


        return view
    }

    private fun initRecyclerView(view: View) {
        adapter = RecipeAdapter(recipeMutableList) { recipe ->
            onItemSelected(recipe)

        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    fun onItemSelected(recipe: Recipe) {
            RecipeIdHolder.recipeId = recipe.id
            Toast.makeText(requireContext(), "${RecipeIdHolder.recipeId}", Toast.LENGTH_SHORT).show()
            parentFragmentManager.commit {
                replace<InfoFragment>(R.id.frameContainer)
                setReorderingAllowed(true)
                addToBackStack("replacement")
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

        Log.d("RecetasGuardadasFrag", "Nombre de usuario: $nombreUsuario")
        return nombreUsuario
    }

    private fun isRecipeSavedAsFavorite() {
        var idFavorite: String? = null
        var username: String? = null
        db.collection("recetasGuardadas").get()
            .addOnSuccessListener { documents ->
                for(document in documents.documents){
                    idFavorite = document.getString("id")
                    val nombre = document.getString("nombre")
                    val tiempo = document.getString("tiempo")
                    val calories = document.getString("calories")
                    val like = document.getBoolean("like")
                    val ingredients = document.get("ingredientes") as List<String>?
                    val ingredientsList: List<String> =
                        ingredients ?: listOf()// Si es nulo, asigna una lista vacía

                    val descripcion = document.getString("descripcion")

                    val img = document.getString("img")

                    val pasosPreparacion = document.get("pasosPreparacion") as List<String>?
                    val pasosList: List<String> = pasosPreparacion ?: listOf()
                    username = document.getString("username")

                }
            }
        var isFavorite = false
        db.collection("recetasGuardadas")
            .whereEqualTo("id", idFavorite)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val like = document.getBoolean("like") ?: false
                    if (like) {
                        val botonLike2 = view?.findViewById<LottieAnimationView>(R.id.botonLike)
                        botonLike2?.setColorFilter(
                            Color.RED // Cambia el color del botón a rojo si está guardado como favorito


                        )
                    }else {
                        val botonLike2 = view?.findViewById<LottieAnimationView>(R.id.botonLike)
                        botonLike2?.setColorFilter(
                            Color.BLACK
                        )

                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error al buscar receta favorita: $exception")
            }
    }

    object RecipeIdHolder {
        var recipeId: String? = null

    }

}
