package com.example.aplikasidiamond.data.model

import androidx.annotation.DrawableRes

data class Product(
    val id: Int,
    val categoryId: Int,
    val nama_produk: String,
    val stok: Int,
    val harga: Int,
    val deskripsi: String,
    val imageData: ByteArray? = null // Data gambar sebagai ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Product
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int = id
}

data class Category(
    val id: Int,
    val name: String,
    @DrawableRes val imageUrl: Int
)

