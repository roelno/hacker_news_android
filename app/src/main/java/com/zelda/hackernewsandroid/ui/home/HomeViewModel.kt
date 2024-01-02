package com.zelda.hackernewsandroid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.ContentExtractor
import com.zelda.hackernewsandroid.News
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    // LiveData for managing news data
    private val _newsList = MutableLiveData<MutableList<News?>>()
    val newsList: LiveData<MutableList<News?>> = _newsList

    fun fetchTopNews() {
        viewModelScope.launch {
            try {
                val storyIds = RetrofitInstance.api.getTopStoryIds().take(6)
                // Initialize the list with nulls
                val tempList = MutableList<News?>(storyIds.size) { null }
                _newsList.postValue(tempList)

                storyIds.forEachIndexed { index, id ->
                    launch {
                        val story = fetchStoryWithContent(id)
                        story?.let {
                            tempList[index] = it
                            _newsList.postValue(tempList)
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    fun clearNewsList() {
        _newsList.value = mutableListOf()
    }


    private suspend fun fetchStoryWithContent(id: Long): News? {
        return withContext(Dispatchers.IO) { // Switch to IO dispatcher for network operation
            try {
                val story = RetrofitInstance.api.getStory(id)
                story.context =
                    ContentExtractor.fetchContent(story.url).take(300) // Fetch and trim the content
                story
            } catch (e: Exception) {
                null // Handle exceptions appropriately
            }
        }
    }


}