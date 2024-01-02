package com.zelda.hackernewsandroid.ui.topstory

import android.util.Log
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

class TopStoryViewModel : ViewModel() {

    private val _newsList = MutableLiveData<MutableList<News?>>()
    val newsList: LiveData<MutableList<News?>> = _newsList

    private var storyIds = listOf<Long>()
    private var lastIndex = 0
    private val pageSize = 5

    var isLoading = false
    var isLastPage = false

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        fetchStoryIds()
    }

    private fun fetchStoryIds() {
        viewModelScope.launch {
            try {
                storyIds = RetrofitInstance.api.getTopStoryIds()
                loadMoreNews()
            } catch (e: Exception) {
                Log.e("TopStoryViewModel", "Error fetching top story IDs", e)
                _errorMessage.postValue("Error fetching data")
            }
        }
    }

    fun loadMoreNews() {
        if (isLoading || isLastPage) return

        isLoading = true
        val endIndex = minOf(lastIndex + pageSize, storyIds.size)

        // Pre-allocate space for the new items
        val currentList = _newsList.value.orEmpty().toMutableList()
        currentList.addAll(List(endIndex - lastIndex) { null })
        _newsList.postValue(currentList)

        viewModelScope.launch {
            storyIds.subList(lastIndex, endIndex).forEachIndexed { index, id ->
                fetchStoryWithContent(id)?.let { newsItem ->
                    currentList[lastIndex + index] = newsItem
                    _newsList.postValue(currentList.filterNotNull().toMutableList())
                }
            }
            lastIndex = endIndex
            isLoading = false
            isLastPage = endIndex == storyIds.size
        }
    }

    private suspend fun fetchStoryWithContent(id: Long): News? {
        return withContext(Dispatchers.IO) {
            try {
                val story = RetrofitInstance.api.getStory(id)
                story.context = ContentExtractor.fetchContent(story.url).take(300)
                story
            } catch (e: Exception) {
                Log.e("TopStoryViewModel", "Error fetching story content for ID: $id", e)
                null
            }
        }
    }

    fun refreshNews() {
        lastIndex = 0
        isLastPage = false
        _newsList.postValue(mutableListOf())
        fetchStoryIds()
    }

}