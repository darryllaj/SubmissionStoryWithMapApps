package com.dicoding.submissionstoryapps.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(token: String): ApiService{
            val logginInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logginInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}