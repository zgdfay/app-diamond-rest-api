package com.example.aplikasidiamond.data.repository

import android.util.Base64
import android.util.Log
import com.example.aplikasidiamond.data.api.ApiProduct
import com.example.aplikasidiamond.data.api.ApiService
import com.example.aplikasidiamond.data.api.RetrofitClient
import com.example.aplikasidiamond.data.model.Product

class ProductRepository {
    private val apiService: ApiService = RetrofitClient.apiService

    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val apiProducts = response.body()!!.data ?: emptyList()
                val products = apiProducts.map { it.toProduct() }
                Result.success(products)
            } else {
                Result.failure(Exception("Gagal mengambil produk: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Mapper function
private fun ApiProduct.toProduct(): Product {
    Log.d("ProductRepository", "Processing product: $nama_produk, gambar length: ${gambar?.length ?: 0}")
    
    val imageData: ByteArray? = gambar?.trim()?.let { base64String ->
        if (base64String.isEmpty()) {
            Log.d("ProductRepository", "Image is empty for $nama_produk")
            null
        } else {
            try {
                // Bersihkan string dari prefix data URL jika ada
                val cleanedBase64 = when {
                    base64String.startsWith("data:image") -> {
                        // Hapus prefix "data:image/xxx;base64,"
                        base64String.substringAfter("base64,")
                    }
                    else -> base64String
                }.replace("\\s".toRegex(), "") // Hapus whitespace
                
                val decoded = Base64.decode(cleanedBase64, Base64.DEFAULT)
                Log.d("ProductRepository", "Image decoded successfully for $nama_produk, size: ${decoded.size} bytes")
                decoded
            } catch (e: IllegalArgumentException) {
                Log.e("ProductRepository", "Failed to decode base64 for $nama_produk: ${e.message}")
                null
            }
        }
    }

    return Product(
        id = id,
        categoryId = categoryId ?: 0,
        nama_produk = nama_produk,
        stok = stok,
        harga = harga,
        deskripsi = deskripsi,
        imageData = imageData
    )
}
