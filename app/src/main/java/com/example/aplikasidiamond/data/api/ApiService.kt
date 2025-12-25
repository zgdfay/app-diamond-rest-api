package com.example.aplikasidiamond.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @GET("produk.php")
    suspend fun getProducts(): Response<ProductResponse>
    
    @GET("transaksi.php")
    suspend fun getTransactions(): Response<TransactionResponse>
    
    @POST("transaksi.php")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): Response<CreateTransactionResponse>
    
    @PUT("transaksi.php")
    suspend fun updateTransaction(
        @Query("id") id: Int,
        @Body request: UpdateTransactionRequest
    ): Response<GenericResponse>
    
    @DELETE("transaksi.php")
    suspend fun deleteTransaction(@Query("id") id: Int): Response<GenericResponse>
}
