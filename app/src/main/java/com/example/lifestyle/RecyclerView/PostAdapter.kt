package com.example.lifestyle.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifestyle.R
import com.example.lifestyle.Recipe2
import com.example.lifestyle.databinding.ElementPostBinding


class PostAdapter(
    private val listRecipe: MutableList<Recipe2>,
    private val onClickListener: (Recipe2) -> Unit,

    ) : RecyclerView.Adapter<PostAdapter.ViewHolderPost>() {

    class ViewHolderPost(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ElementPostBinding.bind(view)

        fun render(recipe: Recipe2, onClickListener: (Recipe2) -> Unit){
            Glide.with(binding.postImg.context).load(recipe.img).into(binding.postImg)


            itemView.setOnClickListener { onClickListener(recipe) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPost {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolderPost(layoutInflater.inflate(R.layout.element_post, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderPost, position: Int) {
        val item = listRecipe[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = listRecipe.size

}