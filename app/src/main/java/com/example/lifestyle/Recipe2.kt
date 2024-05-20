package com.example.lifestyle

data class Recipe2(
    val id:String? = null,
    val username:String?= null,
    val nombre:String?= null,
    val tiempo:String?= null,
    val calorias:String?= null,
    val ingredientes: List<String>? = null,
    val descripcion: String? = null,
    var img: String? = null,
    val pasosPreparacion:List<String>? = null,
    val like:String? = null

    ){

}
