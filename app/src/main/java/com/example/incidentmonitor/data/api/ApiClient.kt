package com.example.incidentmonitor.data.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET

interface OnCallApiService {
    /** Получение списка дежурных сотрудников (справочные данные из внешнего API). */
    @GET("users")
    suspend fun getUsers(): List<ApiUser>
}

object ApiClient {

    private val json = Json { ignoreUnknownKeys = true }

    val service: OnCallApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OnCallApiService::class.java)
    }
}
