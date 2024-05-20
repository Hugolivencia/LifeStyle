package com.example.lifestyle.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.RecyclerView.RecipeAdapter
import com.google.firebase.firestore.FirebaseFirestore


class InfoFragment : Fragment() {
    private lateinit var adapter: RecipeAdapter
    private val recipeMutableList = mutableListOf<Recipe>()
    private var recipeId = RecipeFragment.RecipeIdHolder.recipeId.toString()
    private var recipe = Recipe()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info_fragment, container, false)
        val linearLayoutIngredientes = view.findViewById<LinearLayout>(R.id.linearLayoutInfo)
        val linearLayoutPasos = view.findViewById<LinearLayout>(R.id.linearLayoutPasos)

        val db = FirebaseFirestore.getInstance()


        db.collection("recipes").document(recipeId).get()
            .addOnSuccessListener { document ->

                if (document != null && document.exists()) {

                    // datos del documento
                    val titulo = document.get("nombre") as String
                    val tiempoPreparacion = document.get("tiempo") as String?
                    val ingredients = document.get("ingredientes") as List<String>?
                    val urlImg = document.get("img") as String?
                    val pasosPreparacion = document.get("pasosPreparacion") as List<String>?


                    // Renderizar los datos recibidos en el textView correspondiente
                    val textViewReceta = view.findViewById<TextView>(R.id.titleTextView2)
                    textViewReceta.text = titulo
                    val textViewTiempo = view.findViewById<TextView>(R.id.prepTimeTextView2)
                    textViewTiempo.text = tiempoPreparacion
                    val imgViewImage = view.findViewById<ImageView>(R.id.imageView2)
                    Glide.with(imgViewImage.context).load(urlImg).into(imgViewImage)

                    if (!ingredients.isNullOrEmpty()) {
                        for (ingrediente in ingredients) {

                            val textView = TextView(requireContext())
                            textView.text = ingrediente
                            textView.textSize = 18f
                            textView.setPadding(0, 8, 0, 8)
                            linearLayoutIngredientes.addView(textView)
                        }
                    } else {
                        // No hay ingredientes disponibles
                        Log.d("TAG", "No hay ingredientes disponibles")
                    }

                    if (!pasosPreparacion.isNullOrEmpty()) {
                        for (pasoPreparacion in pasosPreparacion!!) {

                            val textView = TextView(requireContext())
                            textView.text = pasoPreparacion
                            textView.textSize = 18f
                            textView.setPadding(0, 8, 0, 8)
                            linearLayoutPasos.addView(textView)
                        }
                    } else {
                        // No hay ingredientes disponibles
                        Log.d("TAG", "No hay ingredientes disponibles")
                    }


                } else {
                    // El documento no existe o no se ha obtenido
                    Log.d("TAG", "El documento no existe o no se ha obtenido")
                    Log.d("TAG", "La variable recipeId contiene: $recipeId  ")
                }
            }.addOnFailureListener { exception ->
                // Error al obtener los datos
                Log.e("TAG", "Error al obtener los datos: ", exception)
            }

            initRecyclerView(view, requireContext())
        return view
    }

    private fun initRecyclerView(view : View, context: Context) {

        adapter = RecipeAdapter(recipeMutableList) { recipe ->
            Toast.makeText(context, recipeId, Toast.LENGTH_SHORT).show()
            onItemSelected(recipe)
        }
    }

    fun onItemSelected(recipe: Recipe) {
        val recipeId = recipe.nombre!!
        Toast.makeText(requireContext(), recipeId, Toast.LENGTH_SHORT).show()
        Log.d("TAG", "La variable recipeId8 contiene: $recipeId")

    }

}