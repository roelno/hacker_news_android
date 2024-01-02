package com.zelda.hackernewsandroid.ui.topstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.ContentExtractor
import com.zelda.hackernewsandroid.News
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopStoryViewModel : ViewModel() {

    private val _newsList = MutableLiveData<MutableList<News?>>()
    val newsList: LiveData<MutableList<News?>> = _newsList

    private var storyIds = listOf<Long>()
    private var lastIndex = 0
    private val pageSize = 20

    //    var isLoading = false
    var isLoading = MutableLiveData<Boolean>().apply { value = false }
    var isLastPage = false
    private var loadingItemCount = 0

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
        if (isLoading.value == true || isLastPage || loadingItemCount > 0) return

        isLoading.postValue(true)
        val endIndex = minOf(lastIndex + pageSize, storyIds.size)
        val currentList = _newsList.value.orEmpty().toMutableList()
        Log.i("TopStoryVM", "_newsList size is ${_newsList.value?.size} before posting currentList")
        Log.i("TopStoryVM", "currentList size is ${currentList.size} before adding placeholder")
        val placeholders = List(endIndex - lastIndex) { null }
        currentList.addAll(placeholders)
        Log.i("TopStoryVM", "currentList size is ${currentList.size} after adding placeholder")
        _newsList.postValue(currentList)
        Log.i("TopStoryVM", "_newsList size is ${_newsList.value?.size} after posting currentList")
        Log.i("TopStoryVM", "endIndex is $endIndex initially")

        viewModelScope.launch {
            val fetchJobs = mutableListOf<Deferred<Unit>>()

            storyIds.subList(lastIndex, endIndex).forEachIndexed { index, id ->
                loadingItemCount++  // Increment counter
                val fetchJob = async(Dispatchers.IO) {
                    val newsItem = fetchStoryWithContent(id)
                    withContext(Dispatchers.Main) {
                        currentList[lastIndex + index] = newsItem
                        Log.i("TopStoryVM", "currentList size is: ${currentList.size}, lastIndex is $lastIndex, index is $index, endIndex is $endIndex")
                        _newsList.value = currentList.toMutableList()
                        loadingItemCount--  // Decrement counter
                    }
                    Unit // Explicitly return Unit
                }
                fetchJobs.add(fetchJob)
            }

            fetchJobs.awaitAll()
            lastIndex = endIndex
            isLoading.postValue(false)
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