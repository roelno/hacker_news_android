package com.zelda.hackernewsandroid.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: HackerNewsApiService by lazy {  // will only be created when it's first accessed
        Retrofit.Builder() // starts building a new instance of Retrofit
            .baseUrl("https://hacker-news.firebaseio.com/v0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HackerNewsApiService::class.java)
    }
}