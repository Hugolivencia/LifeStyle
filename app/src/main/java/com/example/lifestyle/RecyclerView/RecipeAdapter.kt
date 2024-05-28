package com.example.lifestyle.RecyclerView

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.RecipeFavorite
import com.example.lifestyle.ViewModel.SharedViewModel
import com.example.lifestyle.databinding.ElementRecipeBinding
import com.example.lifestyle.fragment.FragmentUser.RecetasGuardadasFragment
import com.example.lifestyle.fragment.RecipeFragment
import com.example.lifestyle.fragment.RecipeFragment.RecipeIdHolder.recipeId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore




class RecipeAdapter(
    private var listRecipe: List<Recipe>,
    private val onClickListener: (Recipe) -> Unit


) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {


    lateinit var adapter: FavoriteAdapter

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ElementRecipeBinding.bind(view)
        private lateinit var sharedViewModel: SharedViewModel

        fun render(recipe: Recipe,onClickListener: (Recipe) -> Unit) {
            binding.titleTextView.text = recipe.nombre
            binding.prepTimeTextView.text = recipe.tiempo
            binding.descriptionTextView.text = recipe.descripcion
            Glide.with(binding.imageView.context).load(recipe.img).into(binding.imageView)

            itemView.setOnClickListener { onClickListener(recipe) }


            var like = false

            binding.botonLike.setOnClickListener {
                recipeId = recipe.id ?: ""

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recipe.like = true

                    recipeId = recipe.id
                    recuperarRecetaFavorita(recipeId!!)
                    //recuperarRecetaFavortia(recipeId!!)

                }

                like = likeAnimation(binding.botonLike, R.raw.animation_like, like)

            }

        }


        private fun recuperarRecetaFavorita(recipeId: String) {
            val db = FirebaseFirestore.getInstance()
            db.collection("recipes").document(recipeId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Verificar si la receta ya está marcada como favorita
                        if (document.getBoolean("like") == true) {
                            Log.d("Receta favorita", "La receta ya está marcada como favorita")
                            return@addOnSuccessListener
                        }

                        // Crear un objeto RecipeFavorite con los datos de la receta
                        val id = document.getString("id")
                        val username = document.getString("username")
                        val nombre = document.getString("nombre")
                        val tiempo = document.getString("tiempo")
                        val calories = document.getString("calories")
                        val ingredients = document.get("ingredientes") as List<String>?
                        val ingredientsList: List<String> = ingredients ?: emptyList()
                        val descripcion = document.getString("descripcion")
                        val img = document.getString("img")
                        val pasosPreparacion = document.get("pasosPreparacion") as List<String>?
                        val pasosList: List<String> = pasosPreparacion ?: emptyList()

                        // Crear un objeto RecipeFavorite con los datos recuperados
                        val recipeFavorite = RecipeFavorite(
                            id,
                            username,
                            nombre,
                            tiempo,
                            calories,
                            ingredientsList,
                            descripcion,
                            img,
                            pasosList,
                            true // Marcar la receta como favorita
                        )

                        // Subir la receta favorita al documento a la colección recetasFavoritas
                        db.collection("usuarios").document(recuperarUsuario()!!).collection("recetasFavoritas")
                            .add(recipeFavorite)
                            .addOnSuccessListener { documentReference ->
                                // Obtener la ID generada por Firestore
                                val idFirestore = documentReference.id

                                // Actualizar el documento para agregar la ID como un campo
                                documentReference.update("id", idFirestore)
                                    .addOnSuccessListener {
                                        Log.d("Receta guardada", "La receta se guardó correctamente con ID: $idFirestore")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Error", "Error al actualizar el campo 'id' dentro del documento.", e)
                                    }

                                Log.d("Receta guardada", "La receta se guardó correctamente")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Error", "Error al guardar la receta: ${e.message}", e)
                            }
                    } else {
                        Log.d("Error", "No se encontró el documento 'recipes' con ID: $recipeId")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Error", "Error al recuperar la receta: ${e.message}", e)
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


        private fun likeAnimation(
            imageView: LottieAnimationView,
            animation: Int,
            like: Boolean): Boolean {
            if (!like) {
                imageView.setAnimation(animation)
                imageView.playAnimation()
            } else {
                imageView.setImageResource(R.drawable._643770_favorite_heart_like_likes_love_icon)
            }

            return !like
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.element_recipe, parent, false))
    }

    override fun getItemCount(): Int = listRecipe.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listRecipe[position]

        holder.render(item,onClickListener)

    }


}