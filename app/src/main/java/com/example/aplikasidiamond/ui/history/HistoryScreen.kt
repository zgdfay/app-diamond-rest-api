package com.example.aplikasidiamond.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aplikasidiamond.data.model.Transaction
import com.example.aplikasidiamond.ui.viewmodel.ProductViewModel
import com.example.aplikasidiamond.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onTransactionClick: (Int) -> Unit,
    transactionViewModel: TransactionViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel()
) {
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    
    val transactions by transactionViewModel.transactions.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()
    val error by transactionViewModel.error.collectAsState()
    val products by productViewModel.products.collectAsState()
    
    // Pastikan products di-load saat screen pertama kali dibuka
    LaunchedEffect(Unit) {
        if (products.isEmpty()) {
            productViewModel.loadProducts()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Memuat transaksi...")
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { transactionViewModel.refreshTransactions() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            transactions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada riwayat transaksi.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(transactions.reversed()) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            products = products,
                            onClick = { selectedTransaction = transaction }
                        )
                    }
                }
            }
        }
    }
    
    selectedTransaction?.let { transaction ->
        TransactionDetailModal(
            transaction = transaction,
            products = products,
            onDismiss = { selectedTransaction = null }
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    products: List<com.example.aplikasidiamond.data.model.Product>,
    onClick: () -> Unit
) {
    val product = products.find { it.id == transaction.product_id }
    val formattedDate = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(transaction.tanggal)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product?.nama_produk ?: "Produk tidak ditemukan",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Jumlah: ${transaction.qty}")
            Text(text = "Total Harga: Rp ${transaction.total_harga}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = formattedDate, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TransactionDetailModal(
    transaction: Transaction,
    products: List<com.example.aplikasidiamond.data.model.Product>,
    onDismiss: () -> Unit
) {
    val product = products.find { it.id == transaction.product_id }
    val formattedDate = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(transaction.tanggal)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Detail Transaksi",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("ID Transaksi", transaction.id.toString())
                DetailRow("ID Produk", transaction.product_id.toString())
                DetailRow(
                    "Nama Produk",
                    product?.nama_produk ?: "Produk tidak ditemukan"
                )
                DetailRow("Jumlah (Qty)", transaction.qty.toString())
                DetailRow(
                    "Total Harga",
                    "Rp ${transaction.total_harga}",
                    isHighlighted = true
                )
                DetailRow("Tanggal", formattedDate)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
