package com.example.aplikasidiamond.data.model

import androidx.annotation.DrawableRes

data class Product(
    val id: Int,
    val categoryId: Int,
    val nama_produk: String,
    val stok: Int,
    val harga: Int,
    val deskripsi: String,
    val imageUrl: String? = null, // URL gambar dari API
    @DrawableRes val imageRes: Int = 0 // Fallback drawable resource
)

data class Category(
    val id: Int,
    val name: String,
    @DrawableRes val imageUrl: Int
)
