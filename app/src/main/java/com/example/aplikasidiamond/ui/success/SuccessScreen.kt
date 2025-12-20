package com.example.aplikasidiamond.ui.success

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aplikasidiamond.ui.theme.AplikasiDiamondTheme

@Composable
fun SuccessScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Pembelian Berhasil!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Terima kasih telah berbelanja di Toko Diamond",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kembali ke Home")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Histori Transaksi")
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    AplikasiDiamondTheme {
        SuccessScreen(
            onNavigateToHome = {},
            onNavigateToHistory = {}
        )
    }
}

