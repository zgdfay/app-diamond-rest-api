package com.example.aplikasidiamond

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplikasidiamond.ui.detail.DetailScreen
import com.example.aplikasidiamond.ui.detail.TransactionDetailScreen
import com.example.aplikasidiamond.ui.home.HomeScreen
import com.example.aplikasidiamond.ui.product.ProductListScreen
import com.example.aplikasidiamond.ui.theme.AplikasiDiamondTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiDiamondTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Rute 1: Layar utama untuk menampilkan riwayat transaksi
                        composable("home") {
                            HomeScreen(
                                onTransactionClick = { transactionId ->
                                    navController.navigate("transactionDetail/$transactionId")
                                }
                            )
                        }
                        
                        // Rute 2: Layar detail transaksi
                        composable("transactionDetail/{transactionId}") { backStackEntry ->
                            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toInt() ?: 0
                            TransactionDetailScreen(
                                transactionId = transactionId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        // Rute 3: Layar untuk menampilkan daftar produk
                        composable("productList") {
                            ProductListScreen(
                                onProductClick = { productId ->
                                    navController.navigate("detail/$productId")
                                }
                            )
                        }
                        
                        // Rute 4: Layar detail produk
                        composable("detail/{productId}") { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")?.toInt() ?: 0
                            DetailScreen(
                                productId = productId,
                                onNavigateBack = { navController.popBackStack() },
                                onPurchaseComplete = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
