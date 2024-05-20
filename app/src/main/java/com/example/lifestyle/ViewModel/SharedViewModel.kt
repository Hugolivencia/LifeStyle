package com.example.lifestyle.ViewModel

import androidx.lifecycle.ViewModel
import com.example.lifestyle.RecipeFavorite

class SharedViewModel : ViewModel() {
    val favoriteRecipes = mutableListOf<RecipeFavorite>()
}
