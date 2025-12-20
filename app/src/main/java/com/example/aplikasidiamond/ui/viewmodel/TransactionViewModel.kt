package com.example.aplikasidiamond.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasidiamond.data.model.Transaction
import com.example.aplikasidiamond.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    private val repository = TransactionRepository()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadTransactions()
    }
    
    fun loadTransactions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = repository.getTransactions()
                val transactions = result.getOrNull()
                if (transactions != null) {
                    _transactions.value = transactions
                    _isLoading.value = false
                } else {
                    val exception = result.exceptionOrNull()
                    _error.value = exception?.message ?: "Failed to load transactions"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unexpected error occurred"
                _isLoading.value = false
            }
        }
    }
    
    fun createTransaction(
        productId: Int,
        qty: Int,
        totalHarga: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = repository.createTransaction(productId, qty, totalHarga)
                val transaction = result.getOrNull()
                if (transaction != null) {
                    _isLoading.value = false
                    // Call onSuccess first, then reload transactions in background
                    onSuccess()
                    // Reload transactions after creating new one (non-blocking)
                    loadTransactions()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorMessage = exception?.message ?: "Failed to create transaction"
                    _error.value = errorMessage
                    _isLoading.value = false
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error occurred"
                _error.value = errorMessage
                _isLoading.value = false
                onError(errorMessage)
            }
        }
    }
    
    fun refreshTransactions() {
        loadTransactions()
    }
}

