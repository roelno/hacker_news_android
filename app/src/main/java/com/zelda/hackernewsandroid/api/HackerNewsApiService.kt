package com.zelda.hackernewsandroid.api

import com.zelda.hackernewsandroid.News
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApiService {
    @GET("newstories.json")
    suspend fun getNewStoryIds(): List<Long>

    @GET("topstories.json")
    suspend fun getTopStoryIds(): List<Long>

    @GET("item/{id}.json")
    suspend fun getStory(@Path("id") id: Long): News
}
