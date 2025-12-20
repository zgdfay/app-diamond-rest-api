package com.example.aplikasidiamond

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aplikasidiamond.ui.detail.DetailScreen
import com.example.aplikasidiamond.ui.history.HistoryScreen
import com.example.aplikasidiamond.ui.home.BottomNavBar
import com.example.aplikasidiamond.ui.home.HomeScreen
import com.example.aplikasidiamond.ui.success.SuccessScreen
import com.example.aplikasidiamond.ui.theme.AplikasiDiamondTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiDiamondTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute == "home" || currentRoute == "history") {
                            BottomNavBar(
                                currentRoute = currentRoute ?: "home",
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToHistory = {
                                    navController.navigate("history") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") {
                            HomeScreen(
                                onProductClick = { productId ->
                                    navController.navigate("detail/$productId")
                                }
                            )
                        }
                        composable("detail/{productId}") { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")?.toInt() ?: 0
                            DetailScreen(
                                productId = productId,
                                onNavigateBack = { navController.popBackStack() },
                                onPurchaseComplete = {
                                    // Langsung kembali ke home setelah pembelian berhasil
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("success") {
                            SuccessScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToHistory = {
                                    navController.navigate("history") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onTransactionClick = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
