package com.example.videoplayertask.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.pexels.com/"
    val httpClient = OkHttpClient().newBuilder()

    val client = httpClient.addInterceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .get()
            .addHeader("Authorization", "563492ad6f917000010000015a49cea043a1455ca3a7f635ee47f6ec")
            .build()
        chain.proceed(newRequest)
    }.build()

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val apiService = getRetrofit().create(ApiService::class.java)

}