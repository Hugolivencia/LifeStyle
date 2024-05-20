package com.example.lifestyle.Activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.RecyclerView.RecipeAdapter
import com.example.lifestyle.databinding.ActivityInfoBinding
import com.example.lifestyle.databinding.ActivityMain2Binding
import com.example.lifestyle.fragment.RecipeFragment
import com.example.lifestyle.fragment.RecipeFragment.RecipeIdHolder.recipeId
import com.google.firebase.firestore.FirebaseFirestore


class ActivityInfo: AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private lateinit var adapter: RecipeAdapter
    private val recipeMutableList = mutableListOf<Recipe>()
    private var recipeId = RecipeFragment.RecipeIdHolder.recipeId.toString()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val linearLayoutIngredientes = findViewById<LinearLayout>(R.id.linearLayoutInfo2)
        val linearLayoutPasos = findViewById<LinearLayout>(R.id.linearLayoutPasos2)
        val db = FirebaseFirestore.getInstance()



        db.collection("recipes").document(recipeId).get()
            .addOnSuccessListener { document1 ->

                if (document1 != null && document1.exists()) {

                    // datos del documento
                    val titulo = document1.get("nombre") as String?
                    val tiempoPreparacion = document1.get("tiempo") as String?
                    val ingredients = document1.get("ingredientes") as List<String>?
                    val urlImg = document1.get("img") as String?
                    val pasosPreparacion = document1.get("pasosPreparacion") as List<String>?


                    // Renderizar los datos recibidos en el textView correspondiente
                    val textViewReceta = findViewById<TextView>(R.id.titleTextView3)
                    textViewReceta.text = titulo
                    val textViewTiempo = findViewById<TextView>(R.id.prepTimeTextView3)
                    textViewTiempo.text = tiempoPreparacion
                    val imgViewImage = findViewById<ImageView>(R.id.imageView3)
                    Glide.with(imgViewImage.context).load(urlImg).into(imgViewImage)
                    //Log.d("TAG", "La variable img1 contiene:${recipe.img}")
                    //Log.d("TAG", "La variable img2 contiene:$imgViewImage")
                    //Log.d("TAG", "La variable img3 contiene:$urlImg")


                    if (!ingredients.isNullOrEmpty()) {
                        for (ingrediente in ingredients) {

                            val textView = TextView(this)
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

                            val textView = TextView(this)
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


        db.collection("recetasGuardadas").document(recipeId).get()
            .addOnSuccessListener { document1 ->

                if (document1 != null && document1.exists()) {

                    // datos del documento
                    val titulo = document1.get("nombre") as String?
                    val tiempoPreparacion = document1.get("tiempo") as String?
                    val ingredients = document1.get("ingredientes") as List<String>?
                    val urlImg = document1.get("img") as String?
                    val pasosPreparacion = document1.get("pasosPreparacion") as List<String>?


                    // Renderizar los datos recibidos en el textView correspondiente
                    val textViewReceta = findViewById<TextView>(R.id.titleTextView3)
                    textViewReceta.text = titulo
                    val textViewTiempo = findViewById<TextView>(R.id.prepTimeTextView3)
                    textViewTiempo.text = tiempoPreparacion
                    val imgViewImage = findViewById<ImageView>(R.id.imageView3)
                    Glide.with(imgViewImage.context).load(urlImg).into(imgViewImage)
                    //Log.d("TAG", "La variable img1 contiene:${recipe.img}")
                    //Log.d("TAG", "La variable img2 contiene:$imgViewImage")
                    //Log.d("TAG", "La variable img3 contiene:$urlImg")


                    if (!ingredients.isNullOrEmpty()) {
                        for (ingrediente in ingredients) {

                            val textView = TextView(this)
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

                            val textView = TextView(this)
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

        //initRecyclerView(context = this)
    }

}
