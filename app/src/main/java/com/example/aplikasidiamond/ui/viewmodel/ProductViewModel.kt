package com.example.aplikasidiamond.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasidiamond.data.model.Product
import com.example.aplikasidiamond.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = repository.getProducts()
                val products = result.getOrNull()
                if (products != null) {
                    _products.value = products
                    _isLoading.value = false
                } else {
                    val exception = result.exceptionOrNull()
                    _error.value = exception?.message ?: "Failed to load products"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unexpected error occurred"
                _isLoading.value = false
            }
        }
    }
    
    fun refreshProducts() {
        loadProducts()
    }
}

