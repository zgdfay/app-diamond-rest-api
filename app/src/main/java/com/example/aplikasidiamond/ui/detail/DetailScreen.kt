package com.example.aplikasidiamond.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aplikasidiamond.ui.viewmodel.ProductViewModel
import com.example.aplikasidiamond.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    productId: Int,
    onNavigateBack: () -> Unit,
    onPurchaseComplete: () -> Unit,
    productViewModel: ProductViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val isCreatingTransaction by transactionViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    val product = products.find { it.id == productId }
    var quantity by remember { mutableStateOf(1) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (product == null && !isLoading) {
        // Product not found, navigate back
        onNavigateBack()
        return
    }

    if (product == null) {
        // Still loading
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (product.imageData != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(product.imageData)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.nama_produk,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "No Image",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.nama_produk,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Stok: ${product.stok}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rp ${product.harga}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = product.deskripsi,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Icon(Icons.Default.Remove, contentDescription = "Remove")
                    }
                    Text(text = quantity.toString(), style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = {
                        if (quantity < product.stok) {
                            quantity++
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total: Rp ${product.harga * quantity}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        showConfirmDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = quantity > 0 && quantity <= product.stok
                ) {
                    Text(text = "Beli Sekarang")
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Pembelian") },
            text = {
                Column {
                    Text("Apakah Anda yakin ingin membeli:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${product.nama_produk}",
                        fontWeight = FontWeight.Bold
                    )
                    Text("Jumlah: $quantity")
                    Text(
                        text = "Total: Rp ${product.harga * quantity}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val totalHarga = product.harga * quantity
                        showConfirmDialog = false
                        transactionViewModel.createTransaction(
                            productId = productId,
                            qty = quantity,
                            totalHarga = totalHarga,
                            onSuccess = {
                                // Langsung kembali ke home setelah pembelian berhasil
                                onPurchaseComplete()
                            },
                            onError = { error ->
                                // TODO: Show error message
                                // Re-show dialog on error if needed
                            }
                        )
                    },
                    enabled = !isCreatingTransaction
                ) {
                    Text(if (isCreatingTransaction) "Memproses..." else "Ya, Beli")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
