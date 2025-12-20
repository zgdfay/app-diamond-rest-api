package com.example.aplikasidiamond.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aplikasidiamond.R
import com.example.aplikasidiamond.data.model.Product
import com.example.aplikasidiamond.ui.theme.AplikasiDiamondTheme
import com.example.aplikasidiamond.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (Int) -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Refresh products when screen appears or when returning to screen
    LaunchedEffect(Unit) {
        viewModel.refreshProducts()
    }
    
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = remember(searchQuery, products) {
        if (searchQuery.isBlank()) {
            products
        } else {
            products.filter {
                it.nama_produk.contains(searchQuery, ignoreCase = true) ||
                it.deskripsi.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = { TopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Memuat produk...")
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshProducts() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                       ProductSection(
                            title = "Semua Produk",
                            products = filteredProducts,
                            onProductClick = onProductClick
                        )
                        // Tambahkan padding bottom untuk mencegah item terpotong
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
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
            text = "Toko Diamond",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Cari....") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun ProductSection(title: String, products: List<Product>, onProductClick: (Int) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (products.isEmpty()) {
            EmptyState()
        } else {
            val chunkedProducts = products.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                chunkedProducts.forEach { productRow ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        productRow.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(product, onProductClick)
                            }
                        }
                        if (productRow.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onProductClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            ) {
                when {
                    !product.imageUrl.isNullOrEmpty() -> {
                        // Add timestamp to force reload when image changes
                        val imageUrlWithTimestamp = remember(product.id, product.imageUrl) {
                            // Add hash as query parameter to force reload
                            val hash = product.imageUrl.hashCode()
                            if (product.imageUrl.contains("?")) {
                                "$product.imageUrl&v=$hash"
                            } else if (product.imageUrl.contains("data:image")) {
                                // For data URLs, we can't add query params, so use the hash in remember
                                product.imageUrl
                            } else {
                                "$product.imageUrl?v=$hash"
                            }
                        }
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrlWithTimestamp)
                                .crossfade(false) // Disable crossfade to see immediate changes
                                .allowHardware(false) // Important for base64 images
                                .memoryCachePolicy(CachePolicy.DISABLED) // Disable memory cache
                                .diskCachePolicy(CachePolicy.DISABLED) // Disable disk cache
                                .build(),
                            contentDescription = product.nama_produk,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = if (product.imageRes != 0) painterResource(id = product.imageRes) else null,
                            error = if (product.imageRes != 0) painterResource(id = product.imageRes) else null
                        )
                    }
                    product.imageRes != 0 -> {
                        Image(
                            painter = painterResource(id = product.imageRes),
                            contentDescription = product.nama_produk,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.nama_produk,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Stok: ${product.stok}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rp ${product.harga}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Empty State",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = "Tidak ada produk",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Produk untuk kategori ini belum tersedia",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = onNavigateToHistory,
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AplikasiDiamondTheme {
        HomeScreen(onProductClick = {})
    }
}
