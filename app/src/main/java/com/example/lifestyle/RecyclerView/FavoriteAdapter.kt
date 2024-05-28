package com.example.lifestyle.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.lifestyle.RecipeFavorite
import com.example.lifestyle.databinding.ElementFavoriteBinding

class FavoriteAdapter(
    private val listRecipeFavorite: MutableList<RecipeFavorite>,
    private val onClickListener: (RecipeFavorite) -> Unit,
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolderFavorite>() {

    class ViewHolderFavorite(private val binding: ElementFavoriteBinding,) : RecyclerView.ViewHolder(binding.root) {

        fun render(recipe: RecipeFavorite ,onClickListener: (RecipeFavorite) -> Unit) {
            Glide.with(binding.postImgFavorite.context).load(recipe.img).into(binding.postImgFavorite)

            itemView.setOnClickListener { onClickListener(recipe) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFavorite {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ElementFavoriteBinding.inflate(layoutInflater, parent, false)
        return ViewHolderFavorite(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderFavorite, position: Int) {
        val item = listRecipeFavorite[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = listRecipeFavorite.size

    fun updateList(newList: List<RecipeFavorite>) {
        listRecipeFavorite.clear()
        listRecipeFavorite.addAll(newList)
        notifyDataSetChanged()
    }
}
