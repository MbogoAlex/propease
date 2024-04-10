package com.propertymanagement.tms.container

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.propertymanagement.tms.network.ApiService
import com.propertymanagement.tms.network.NetworkApiRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val apiRepository: NetworkApiRepository
}

class PropEaseMainContainer(private val context: Context): AppContainer{
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    private val baseUrl = "http://172.105.90.112:8080/pManager/"
//    private val baseUrl = "http://192.168.47.6:8080/pManager/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    override val apiRepository: NetworkApiRepository by lazy {
        NetworkApiRepository(retrofitService)
    }
}