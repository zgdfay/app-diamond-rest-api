package com.example.aplikasidiamond.data.repository

import com.example.aplikasidiamond.BuildConfig
import com.example.aplikasidiamond.R
import com.example.aplikasidiamond.data.api.ApiProduct
import com.example.aplikasidiamond.data.api.ApiService
import com.example.aplikasidiamond.data.api.RetrofitClient
import com.example.aplikasidiamond.data.model.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                Result.failure(Exception("Failed to fetch products: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Mapper function
private fun ApiProduct.toProduct(): Product {
    // Map gambar dari API
    // Untuk longblob di database, biasanya API mengembalikan sebagai base64 string
    val baseUrl = BuildConfig.BASE_URL.removeSuffix("/")
    val imageUrl = try {
        gambar?.let { img ->
            // Trim whitespace yang mungkin ada
            val trimmedImg = img.trim()
            
            // Jika kosong setelah trim, return null
            if (trimmedImg.isEmpty()) return@let null
            
            when {
                // Full URL (http/https)
                trimmedImg.startsWith("http://") || trimmedImg.startsWith("https://") -> trimmedImg
                // Base64 data URL (sudah lengkap dengan prefix)
                trimmedImg.startsWith("data:image") -> trimmedImg
                // Untuk longblob dari database PHP, API mengembalikan base64 string langsung
                // Jika string panjang dan tidak terlihat seperti URL/path, langsung treat sebagai base64
                trimmedImg.length > 50 -> {
                    // Bersihkan base64 dari whitespace dan newline
                    val cleanedBase64 = trimmedImg.replace("\\s".toRegex(), "").replace("\n", "").replace("\r", "")
                    if (cleanedBase64.isEmpty()) return@let null
                    
                    // Validasi bahwa ini adalah base64 (hanya karakter base64)
                    val base64Pattern = Regex("^[A-Za-z0-9+/=]+$")
                    if (base64Pattern.matches(cleanedBase64)) {
                        // Deteksi tipe gambar dari base64
                        val imageType = try {
                            detectImageType(cleanedBase64)
                        } catch (e: Exception) {
                            "jpeg" // Default ke jpeg untuk longblob
                        }
                        "data:image/$imageType;base64,$cleanedBase64"
                    } else {
                        // Jika bukan base64, treat sebagai relative path
                        if (trimmedImg.startsWith("/")) {
                            "$baseUrl$trimmedImg"
                        } else {
                            "$baseUrl/$trimmedImg"
                        }
                    }
                }
                // Base64 string pendek - deteksi dengan pattern
                isBase64String(trimmedImg) -> {
                    // Bersihkan base64 dari whitespace dan newline
                    val cleanedBase64 = trimmedImg.replace("\\s".toRegex(), "").replace("\n", "").replace("\r", "")
                    if (cleanedBase64.isEmpty()) return@let null
                    
                    // Coba deteksi format gambar dari base64
                    val imageType = detectImageType(cleanedBase64)
                    "data:image/$imageType;base64,$cleanedBase64"
                }
                // Relative path dengan slash
                trimmedImg.startsWith("/") -> "$baseUrl$trimmedImg"
                // Endpoint khusus untuk gambar (misalnya gambar.php?id=1)
                trimmedImg.contains(".php") || trimmedImg.contains("?") -> "$baseUrl/$trimmedImg"
                // Relative path tanpa slash
                else -> "$baseUrl/$trimmedImg"
            }
        }
    } catch (e: Exception) {
        // Jika ada error saat processing gambar, return null
        null
    }
    
    // Map categoryId dari kategori string atau categoryId int
    val mappedCategoryId = when {
        categoryId != null -> categoryId
        kategori != null -> {
            // Map kategori string ke ID (sesuaikan dengan database Anda)
            when (kategori.lowercase()) {
                "1", "mobile legends", "ml" -> 1
                "2", "free fire", "ff" -> 2
                "3", "pubg mobile", "pubg" -> 3
                "4", "genshin impact", "genshin" -> 4
                "5", "roblox" -> 5
                "6", "stumble guys", "stumble" -> 6
                "7", "honkai star rail", "honkai" -> 7
                "8", "call of duty mobile", "codm" -> 8
                "9", "free fire max", "ffmax" -> 9
                else -> 0
            }
        }
        else -> 0
    }
    
    // Fallback image berdasarkan category
    val imageRes = when (mappedCategoryId) {
        1 -> R.drawable.mlbb
        2 -> R.drawable.ff
        3 -> R.drawable.pubg
        4 -> R.drawable.genshin
        5 -> R.drawable.roblox
        6 -> R.drawable.stumble
        7 -> R.drawable.honkai_star
        8 -> R.drawable.codm
        9 -> R.drawable.ffmax
        else -> 0
    }
    
    return Product(
        id = id,
        categoryId = mappedCategoryId,
        nama_produk = nama_produk,
        stok = stok,
        harga = harga,
        deskripsi = deskripsi,
        imageUrl = imageUrl,
        imageRes = imageRes
    )
}

// Helper function untuk mendeteksi apakah string adalah base64
private fun isBase64String(str: String): Boolean {
    if (str.isEmpty()) return false
    
    // Base64 gambar biasanya panjang (minimal 20 karakter)
    if (str.length < 20) return false
    
    // Base64 hanya mengandung karakter: A-Z, a-z, 0-9, +, /, = (padding)
    // Hapus whitespace, newline, dan carriage return dulu
    val cleaned = str.replace("\\s".toRegex(), "").replace("\n", "").replace("\r", "")
    if (cleaned.isEmpty()) return false
    
    // Cek pattern base64
    val base64Pattern = Regex("^[A-Za-z0-9+/=]+$")
    if (!base64Pattern.matches(cleaned)) return false
    
    // Base64 string biasanya tidak mengandung karakter khusus selain +, /, =
    // Panjang harus kelipatan 4 (setelah padding) atau mendekati
    val lengthWithoutPadding = cleaned.replace("=", "").length
    return lengthWithoutPadding > 0
}

// Helper function untuk mendeteksi tipe gambar dari base64
private fun detectImageType(base64: String): String {
    // Cek magic bytes di awal base64
    // JPEG: FF D8 FF
    // PNG: 89 50 4E 47
    // GIF: 47 49 46 38
    // WebP: 52 49 46 46
    
    return try {
        // Bersihkan base64 dari whitespace dan newline
        val cleanedBase64 = base64.replace("\\s".toRegex(), "")
        
        val bytes = android.util.Base64.decode(cleanedBase64, android.util.Base64.DEFAULT)
        if (bytes.size >= 4) {
            when {
                // JPEG: FF D8 FF
                bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() && bytes[2] == 0xFF.toByte() -> "jpeg"
                // PNG: 89 50 4E 47
                bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte() -> "png"
                // GIF: 47 49 46 38
                bytes[0] == 0x47.toByte() && bytes[1] == 0x49.toByte() && bytes[2] == 0x46.toByte() && bytes[3] == 0x38.toByte() -> "gif"
                // WebP: 52 49 46 46
                bytes[0] == 0x52.toByte() && bytes[1] == 0x49.toByte() && bytes[2] == 0x46.toByte() && bytes[3] == 0x46.toByte() -> "webp"
                else -> "jpeg" // Default ke jpeg untuk longblob
            }
        } else {
            "jpeg" // Default ke jpeg untuk longblob
        }
    } catch (e: Exception) {
        // Jika decode gagal, default ke jpeg (kebanyakan longblob adalah jpeg)
        "jpeg"
    }
}

