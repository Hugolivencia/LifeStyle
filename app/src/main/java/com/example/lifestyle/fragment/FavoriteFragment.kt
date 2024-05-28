package com.example.lifestyle.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifestyle.Activities.login.ActivityRegister
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.RecyclerView.RecipeAdapter
import com.example.lifestyle.RecyclerView.SearchAdapter
import com.google.firebase.firestore.FirebaseFirestore


class FavoriteFragment : Fragment() {
    private lateinit var adapter: SearchAdapter
    private val recipeMutableList = mutableListOf<Recipe>()
    var nombre: String? = null
    val db = FirebaseFirestore.getInstance()
    var activityRegister = ActivityRegister()
    var username = activityRegister.username
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        val text = view.findViewById<TextView>(R.id.txtSearchRecipe)

        // Recuperar todas las recetas
        recipeMutableList.clear()
        db.collection("recipes").get()
            .addOnSuccessListener { documents ->
                for (document in documents.documents) {
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
                    val recipe = Recipe(id,username,nombre, tiempo, calories, ingredientsList, descripcion , img , pasosList)
                    recipeMutableList.add(recipe)

                }
                // Notificar al adaptador sobre el cambio en los datos
                adapter.notifyDataSetChanged()
            }.addOnFailureListener { exception ->
                // Manejar el error si la recuperación de datos falla
                Toast.makeText(requireContext(), "Error al recuperar las recetas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }



        text.addTextChangedListener { userFilter ->
            var filtrarNombre = recipeMutableList.filter { recipe ->
                val nombreCoincide = recipe.nombre!!.lowercase().contains(userFilter.toString().lowercase())
                val tiempoCoincide = recipe.tiempo?.lowercase()?.contains(userFilter.toString().lowercase()) ?: false
                val ingredienteFiltro = recipe.ingredientes?.contains(userFilter.toString().lowercase()) ?:false
                // Devuelve verdadero si el nombre o el tiempo coincide con el filtro
                nombreCoincide || tiempoCoincide||ingredienteFiltro
            }
                adapter.updateRecipes(filtrarNombre)
        }


        initRecyclerView(view)

        return view
    }


    private fun initRecyclerView(view : View) {
        adapter = SearchAdapter(recipeMutableList) { recipe ->
            onItemSelected(recipe)
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    fun onItemSelected(recipe: Recipe) {
        RecipeFragment.RecipeIdHolder.recipeId = recipe.nombre
        //Toast.makeText(requireContext(), "$recipeId", Toast.LENGTH_SHORT).show()
        parentFragmentManager.commit {
            replace<InfoFragment>(R.id.frameContainer)
            setReorderingAllowed(true)
            addToBackStack("replacement")

        }
    }

}