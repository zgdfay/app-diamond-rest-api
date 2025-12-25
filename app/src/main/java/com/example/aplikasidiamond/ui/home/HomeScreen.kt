package com.example.aplikasidiamond.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplikasidiamond.data.model.Product
import com.example.aplikasidiamond.data.model.Transaction
import com.example.aplikasidiamond.ui.theme.AplikasiDiamondTheme
import com.example.aplikasidiamond.ui.viewmodel.ProductViewModel
import com.example.aplikasidiamond.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTransactionClick: (Int) -> Unit = {},
    viewModel: TransactionViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val products by productViewModel.products.collectAsState()
    val productsLoading by productViewModel.isLoading.collectAsState()

    // State for create dialog
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var qty by remember { mutableIntStateOf(1) }
    var isSubmitting by remember { mutableStateOf(false) }
    
    // State for edit dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var editSelectedProduct by remember { mutableStateOf<Product?>(null) }
    var editQty by remember { mutableIntStateOf(1) }
    var isEditSubmitting by remember { mutableStateOf(false) }
    
    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    // Refresh data when the screen appears
    LaunchedEffect(Unit) {
        viewModel.loadTransactions()
        productViewModel.loadProducts()
    }

    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Buat Transaksi Baru")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Memuat riwayat transaksi...")
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    }
                }
                transactions.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    TransactionList(
                        transactions = transactions,
                        products = products,
                        onTransactionClick = onTransactionClick,
                        onEditClick = { transaction ->
                            editingTransaction = transaction
                            editQty = transaction.qty
                            // Find the product for this transaction
                            editSelectedProduct = products.find { it.id == transaction.product_id }
                            showEditDialog = true
                        },
                        onDeleteClick = { transaction ->
                            deletingTransaction = transaction
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Create Transaction Dialog
    if (showCreateDialog) {
        TransactionDialog(
            title = "Transaksi Baru",
            products = products,
            productsLoading = productsLoading,
            selectedProduct = selectedProduct,
            qty = qty,
            isSubmitting = isSubmitting,
            onProductSelected = { product ->
                selectedProduct = product
            },
            onQtyChanged = { newQty ->
                qty = newQty
            },
            onDismiss = {
                showCreateDialog = false
                selectedProduct = null
                qty = 1
            },
            onConfirm = {
                selectedProduct?.let { product ->
                    isSubmitting = true
                    val totalHarga = product.harga * qty
                    viewModel.createTransaction(
                        productId = product.id,
                        qty = qty,
                        totalHarga = totalHarga,
                        onSuccess = {
                            isSubmitting = false
                            showCreateDialog = false
                            selectedProduct = null
                            qty = 1
                        },
                        onError = {
                            isSubmitting = false
                        }
                    )
                }
            }
        )
    }
    
    // Edit Transaction Dialog
    if (showEditDialog && editingTransaction != null) {
        TransactionDialog(
            title = "Edit Transaksi #${editingTransaction!!.id}",
            products = products,
            productsLoading = productsLoading,
            selectedProduct = editSelectedProduct,
            qty = editQty,
            isSubmitting = isEditSubmitting,
            onProductSelected = { product ->
                editSelectedProduct = product
            },
            onQtyChanged = { newQty ->
                editQty = newQty
            },
            onDismiss = {
                showEditDialog = false
                editingTransaction = null
                editSelectedProduct = null
                editQty = 1
            },
            onConfirm = {
                editSelectedProduct?.let { product ->
                    isEditSubmitting = true
                    val totalHarga = product.harga * editQty
                    viewModel.updateTransaction(
                        id = editingTransaction!!.id,
                        productId = product.id,
                        qty = editQty,
                        totalHarga = totalHarga,
                        onSuccess = {
                            isEditSubmitting = false
                            showEditDialog = false
                            editingTransaction = null
                            editSelectedProduct = null
                            editQty = 1
                        },
                        onError = {
                            isEditSubmitting = false
                        }
                    )
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog && deletingTransaction != null) {
        DeleteConfirmationDialog(
            transaction = deletingTransaction!!,
            isDeleting = isDeleting,
            onDismiss = {
                showDeleteDialog = false
                deletingTransaction = null
            },
            onConfirm = {
                isDeleting = true
                viewModel.deleteTransaction(
                    id = deletingTransaction!!.id,
                    onSuccess = {
                        isDeleting = false
                        showDeleteDialog = false
                        deletingTransaction = null
                    },
                    onError = {
                        isDeleting = false
                    }
                )
            }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    transaction: Transaction,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = { 
            Text(
                "Hapus Transaksi",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Text("Apakah Anda yakin ingin menghapus Transaksi #${transaction.id}? Tindakan ini tidak dapat dibatalkan.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text("Hapus")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
    title: String,
    products: List<Product>,
    productsLoading: Boolean,
    selectedProduct: Product?,
    qty: Int,
    isSubmitting: Boolean,
    onProductSelected: (Product) -> Unit,
    onQtyChanged: (Int) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val totalHarga = selectedProduct?.let { it.harga * qty } ?: 0
    val formattedTotalHarga = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(totalHarga)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Dropdown
                Text(
                    text = "Pilih Produk",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedProduct?.nama_produk ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Pilih produk...") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.clickable { dropdownExpanded = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dropdownExpanded = true }
                    )
                    
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        if (productsLoading) {
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                        Text("Memuat produk...")
                                    }
                                },
                                onClick = {}
                            )
                        } else if (products.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Tidak ada produk tersedia") },
                                onClick = {}
                            )
                        } else {
                            products.forEach { product ->
                                val productPrice = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(product.harga)
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(
                                                text = product.nama_produk,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = productPrice,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    onClick = {
                                        onProductSelected(product)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Quantity Input
                Text(
                    text = "Jumlah (Qty)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (qty > 1) onQtyChanged(qty - 1) },
                        enabled = qty > 1
                    ) {
                        Text("-")
                    }
                    
                    OutlinedTextField(
                        value = qty.toString(),
                        onValueChange = { value ->
                            val newQty = value.filter { it.isDigit() }.toIntOrNull() ?: 1
                            onQtyChanged(if (newQty < 1) 1 else newQty)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                        singleLine = true
                    )
                    
                    OutlinedButton(
                        onClick = { onQtyChanged(qty + 1) }
                    ) {
                        Text("+")
                    }
                }

                // Total Price (Readonly)
                Text(
                    text = "Total Harga",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = formattedTotalHarga,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    enabled = false
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selectedProduct != null && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Riwayat Transaksi",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    products: List<Product>,
    onTransactionClick: (Int) -> Unit,
    onEditClick: (Transaction) -> Unit,
    onDeleteClick: (Transaction) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(transactions) { transaction ->
            val productName = products.find { it.id == transaction.product_id }?.nama_produk ?: "Produk #${transaction.product_id}"
            TransactionItem(
                transaction = transaction,
                productName = productName,
                onClick = { onTransactionClick(transaction.id) },
                onEditClick = { onEditClick(transaction) },
                onDeleteClick = { onDeleteClick(transaction) }
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    productName: String,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(transaction.total_harga)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi #${transaction.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Produk: $productName")

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Jumlah: ${transaction.qty}")

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total: $formattedPrice",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Tidak Ada Transaksi",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = "Belum Ada Transaksi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tekan tombol '+' untuk memulai belanja.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AplikasiDiamondTheme {
        HomeScreen()
    }
}
