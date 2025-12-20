package com.example.aplikasidiamond.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("produk.php")
    suspend fun getProducts(): Response<ProductResponse>
    
    @GET("transaksi.php")
    suspend fun getTransactions(): Response<TransactionResponse>
    
    @POST("transaksi.php")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): Response<CreateTransactionResponse>
}

