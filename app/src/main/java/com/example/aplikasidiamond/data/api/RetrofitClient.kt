package com.example.aplikasidiamond.data.api

import com.example.aplikasidiamond.BuildConfig
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

object RetrofitClient {
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

