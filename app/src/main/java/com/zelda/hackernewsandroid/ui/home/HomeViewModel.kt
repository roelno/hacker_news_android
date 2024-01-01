package com.zelda.hackernewsandroid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.ContentExtractor
import com.zelda.hackernewsandroid.News
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    // LiveData for managing news data
    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> = _newsList

//    fun fetchTopNews() {
//        viewModelScope.launch {
//            try {
//                val storyIds = RetrofitInstance.api.getTopStoryIds()
//                val stories = storyIds.take(50) // Taking only the first 50 stories
//                    .map { id -> async { RetrofitInstance.api.getStory(id) } }
//                    .awaitAll()
//                    .filterNotNull()
//
//                _newsList.postValue(stories)
//            } catch (e: Exception) {
//                // Handle exceptions for fetching story IDs and individual stories
//            }
//        }
//    }

    fun fetchTopNews() {
        viewModelScope.launch {
            try {
                val storyIds = RetrofitInstance.api.getTopStoryIds()
                val stories = storyIds.take(50) // Taking only the first 50 stories
                    .map { id -> async { fetchStoryWithContent(id) } }
                    .awaitAll()
                    .filterNotNull()

                _newsList.postValue(stories)
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

//    private suspend fun fetchStoryWithContent(id: Long): News? {
//        return try {
//            val story = RetrofitInstance.api.getStory(id)
//            story.context = fetchContent(story.url).take(60) // Fetch and trim the content
//            story
//        } catch (e: Exception) {
//            null
//        }
//    }

    private suspend fun fetchStoryWithContent(id: Long): News? {
        return withContext(Dispatchers.IO) { // Switch to IO dispatcher for network operation
            try {
                val story = RetrofitInstance.api.getStory(id)
                story.context = ContentExtractor.fetchContent(story.url).take(300) // Fetch and trim the content
                story
            } catch (e: Exception) {
                null // Handle exceptions appropriately
            }
        }
    }

//    private suspend fun fetchContent(url: String): String {
//        // Logic to fetch content from URL
//        // This needs to be implemented based on how you can fetch content from the URLs
//        // It could involve making an HTTP request and parsing the response
//    }

}