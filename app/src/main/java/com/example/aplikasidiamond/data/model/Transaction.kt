package com.example.aplikasidiamond.data.model

import java.util.Date

data class Transaction(
    val id: Int,
    val product_id: Int,
    val qty: Int,
    val total_harga: Int,
    val tanggal: Date
)
