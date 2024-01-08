package com.zelda.hackernewsandroid.api

import com.zelda.hackernewsandroid.Items
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApiService {
    @GET("newstories.json")
    suspend fun getNewStoryIds(): List<Long>

    @GET("topstories.json")
    suspend fun getTopStoryIds(): List<Long>

    @GET("beststories.json")
    suspend fun getBestStoryIds(): List<Long>

    @GET("item/{id}.json")
    suspend fun getItem(@Path("id") id: Long): Items


}
