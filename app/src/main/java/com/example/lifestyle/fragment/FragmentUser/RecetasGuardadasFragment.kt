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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifestyle.Activities.ActivityInfo
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.RecipeFavorite
import com.example.lifestyle.RecyclerView.FavoriteAdapter
import com.example.lifestyle.RecyclerView.RecipeAdapter
import com.example.lifestyle.ViewModel.SharedViewModel
import com.example.lifestyle.fragment.InfoFragment
import com.example.lifestyle.fragment.RecipeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.file.DirectoryIteratorException

class RecetasGuardadasFragment : Fragment() {
    private val recipeFavoriteList = mutableListOf<RecipeFavorite>()
    private lateinit var adapter: FavoriteAdapter
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recetas_guardadas, container, false)
        recuperarUsuario()
        initRecyclerView(view)
        var recipeFavorite = RecipeFavorite()
        recipeFavoriteList.clear()
        var nombre:String?

        db.collection("usuarios")
            .document(recuperarUsuario()!!)
            .collection("recetasFavoritas")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents.documents) {
                    val id = document.getString("id")
                    val username = document.getString("username")
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
                    recipeFavorite = RecipeFavorite(
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

                    recipeFavoriteList.add(recipeFavorite)
                    adapter.notifyDataSetChanged()
                }


                // Notificar al adaptador sobre el cambio en los datos


            }.addOnFailureListener { exception ->
                // Manejar el error si la recuperación de datos falla
                Toast.makeText(
                    requireContext(),
                    "Error al recuperar las recetas: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        

        return view
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

    override fun onResume() {
        super.onResume()
        // No necesitas llamar notifyDataSetChanged() aquí, ya que el adaptador se actualiza automáticamente cuando cambia el conjunto de datos.
        val activity = requireActivity()
        activity.onBackPressedDispatcher.addCallback(this) {}
    }

    private fun initRecyclerView(view: View) {
        adapter = FavoriteAdapter(recipeFavoriteList) { recipe ->
            onItemSelected(recipe)
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewLike)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun onItemSelected(recipe: RecipeFavorite) {
        RecipeFragment.RecipeIdHolder.recipeId = recipe.id
        Toast.makeText(requireContext(), "${RecipeFragment.RecipeIdHolder.recipeId}", Toast.LENGTH_SHORT).show()
        Log.d("Receta favorita","id: ${RecipeFragment.RecipeIdHolder.recipeId}")
        val intent = Intent(requireContext(), ActivityInfo::class.java)
        //intent.putExtra("clave", valor)
        startActivity(intent)
    }
}
