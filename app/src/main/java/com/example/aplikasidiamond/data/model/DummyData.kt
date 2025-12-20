package com.example.aplikasidiamond.data.model

import com.example.aplikasidiamond.R
import java.util.Date

object DummyData {
    val categories = listOf(
        Category(0, "Semua", 0),
        Category(1, "Mobile Legends: Bang Bang", R.drawable.mlbb),
        Category(2, "Free Fire", R.drawable.ff),
        Category(3, "PUBG Mobile", R.drawable.pubg),
        Category(4, "Genshin Impact", R.drawable.genshin),
        Category(5, "Roblox", R.drawable.roblox),
        Category(6, "Stumble Guys", R.drawable.stumble),
        Category(7, "Honkai: Star Rail", R.drawable.honkai_star),
        Category(8, "Call of Duty Mobile", R.drawable.codm),
        Category(9, "Free Fire MAX", R.drawable.ffmax),
    )

    val topSelling = listOf(
        Product(1, 1, "100 Diamonds", 999, 25000, "Beli 100 Diamond Mobile Legends", null, R.drawable.mlbb),
        Product(2, 1, "250 Diamonds", 999, 60000, "Beli 250 Diamond Mobile Legends", null, R.drawable.mlbb),
        Product(3, 2, "100 Diamonds", 999, 20000, "Beli 100 Diamond Free Fire", null, R.drawable.ff),
        Product(4, 2, "200 Diamonds", 999, 40000, "Beli 200 Diamond Free Fire", null, R.drawable.ff),
    )

    val transactions = listOf<Transaction>()
}