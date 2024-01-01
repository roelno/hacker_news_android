package com.zelda.hackernewsandroid.api

import com.zelda.hackernewsandroid.News
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApiService {
    @GET("item/{id}.json")
    suspend fun getStory(@Path("id") id: Int): News

    @GET("/newstories")
    suspend fun getNewStories(@Path("id") id: Int): List<News>
}
