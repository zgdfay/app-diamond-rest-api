package com.example.aplikasidiamond.data.repository

import com.example.aplikasidiamond.data.api.ApiTransaction
import com.example.aplikasidiamond.data.api.ApiService
import com.example.aplikasidiamond.data.api.CreateTransactionRequest
import com.example.aplikasidiamond.data.api.RetrofitClient
import com.example.aplikasidiamond.data.api.UpdateTransactionRequest
import com.example.aplikasidiamond.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionRepository {
    private val apiService: ApiService = RetrofitClient.apiService
    
    suspend fun getTransactions(): Result<List<Transaction>> {
        return try {
            val response = apiService.getTransactions()
            if (response.isSuccessful && response.body() != null) {
                val apiTransactions = response.body()!!.data ?: emptyList()
                val transactions = apiTransactions.map { it.toTransaction() }
                Result.success(transactions)
            } else {
                Result.failure(Exception("Failed to fetch transactions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createTransaction(
        productId: Int,
        qty: Int,
        totalHarga: Int
    ): Result<Unit> {
        return try {
            val request = CreateTransactionRequest(
                product_id = productId,
                qty = qty,
                total_harga = totalHarga
            )
            val response = apiService.createTransaction(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTransaction(
        id: Int,
        productId: Int,
        qty: Int,
        totalHarga: Int
    ): Result<Unit> {
        return try {
            val request = UpdateTransactionRequest(
                id = id,
                product_id = productId,
                qty = qty,
                total_harga = totalHarga
            )
            val response = apiService.updateTransaction(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTransaction(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteTransaction(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Mapper function
private fun ApiTransaction.toTransaction(): Transaction {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = try {
        dateFormat.parse(tanggal) ?: Date()
    } catch (e: Exception) {
        Date()
    }
    
    return Transaction(
        id = id,
        product_id = product_id,
        qty = qty,
        total_harga = total_harga,
        tanggal = date
    )
}

