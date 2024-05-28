package com.example.lifestyle.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.databinding.ElementRecipeBinding

class SearchAdapter (
        private var listRecipe: List<Recipe>,
        private val onClickListener: (Recipe) -> Unit,

        ) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val binding = ElementRecipeBinding.bind(view)

            fun render(recipe: Recipe, onClickListener: (Recipe) -> Unit){
                binding.titleTextView.text = recipe.nombre
                binding.prepTimeTextView.text = recipe.tiempo
                binding.descriptionTextView.text = recipe.descripcion
                Glide.with(binding.imageView.context).load(recipe.img).into(binding.imageView)


                itemView.setOnClickListener { onClickListener(recipe) }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ViewHolder(layoutInflater.inflate(R.layout.element_recipe, parent, false))
        }

        override fun getItemCount(): Int = listRecipe.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = listRecipe[position]
            holder.render(item, onClickListener)

        }

        fun updateRecipes(listRecipe: List<Recipe>){
            this.listRecipe = listRecipe
            notifyDataSetChanged()
        }

    }

