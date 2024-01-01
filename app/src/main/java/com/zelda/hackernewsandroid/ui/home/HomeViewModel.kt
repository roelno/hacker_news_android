package com.zelda.hackernewsandroid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.News
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // LiveData for managing news data
    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> = _newsList

    fun fetchTopNews() {
        viewModelScope.launch {
            try {
                val storyIds = RetrofitInstance.api.getTopStoryIds()
                val stories = storyIds.take(50) // Taking only the first 50 stories
                    .map { id -> async { RetrofitInstance.api.getStory(id) } }
                    .awaitAll()
                    .filterNotNull()

                _newsList.postValue(stories)
            } catch (e: Exception) {
                // Handle exceptions for fetching story IDs and individual stories
            }
        }
    }

}