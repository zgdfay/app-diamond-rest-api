package com.example.aplikasidiamond.data.api

import com.google.gson.annotations.SerializedName

data class ApiProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("kategori") val kategori: String? = null, // Field dari API
    @SerializedName("category_id") val categoryId: Int? = null, // Fallback
    @SerializedName("nama_produk") val nama_produk: String,
    @SerializedName("stok") val stok: Int,
    @SerializedName("harga") val harga: Int,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("gambar") val gambar: String? = null
)

data class ApiTransaction(
    @SerializedName("id") val id: Int,
    @SerializedName("product_id") val product_id: Int,
    @SerializedName("qty") val qty: Int,
    @SerializedName("total_harga") val total_harga: Int,
    @SerializedName("tanggal") val tanggal: String
)

data class ApiResponse<T>(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)

data class ProductResponse(
    @SerializedName("response") val response: Int? = null, // Field dari API PHP
    @SerializedName("status") val status: String? = null, // Fallback
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<ApiProduct>?
)

data class TransactionResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<ApiTransaction>?
)

data class CreateTransactionRequest(
    @SerializedName("product_id") val product_id: Int,
    @SerializedName("qty") val qty: Int,
    @SerializedName("total_harga") val total_harga: Int
)

data class CreateTransactionResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: ApiTransaction?
)

