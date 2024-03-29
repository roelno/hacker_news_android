package com.zelda.hackernewsandroid


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StoriesViewModel : ViewModel() {
    private val _newsList = MutableLiveData<MutableList<Items?>>()
    val newsList: LiveData<MutableList<Items?>> = _newsList

    private var storyIds = listOf<Long>()
    private var lastIndex = 0
    private val pageSize = 20

    var isLoading = MutableLiveData<Boolean>().apply { value = false }
    var isLastPage = false
    private var loadingItemCount = 0

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun fetchStoryIds(storyType: StoryType) {
        storyType?.let { type ->
            viewModelScope.launch {
                try {
                    storyIds = when (type) {
                        StoryType.TOP -> RetrofitInstance.api.getTopStoryIds()
                        StoryType.BEST -> RetrofitInstance.api.getBestStoryIds()
                        StoryType.NEW -> RetrofitInstance.api.getNewStoryIds()
                    }
                    loadMoreNews()
                } catch (e: Exception) {
                    // Handle the exception
                }
            }
        } ?: Log.e("StoryViewModel", "StoryType not set")
    }





    fun loadMoreNews() {
        if (isLoading.value == true || isLastPage || loadingItemCount > 0) return

        isLoading.postValue(true)
        val endIndex = minOf(lastIndex + pageSize, storyIds.size)
        val currentList = _newsList.value.orEmpty().toMutableList()
        val placeholders = List(endIndex - lastIndex) { null }
        currentList.addAll(placeholders)
        _newsList.postValue(currentList)

        viewModelScope.launch {
            val fetchJobs = mutableListOf<Deferred<Unit>>()

            storyIds.subList(lastIndex, endIndex).forEachIndexed { index, id ->
                loadingItemCount++
                val fetchJob = async(Dispatchers.IO) {
                    val newsItem = fetchStoryWithContent(id)
                    withContext(Dispatchers.Main) {
                        currentList[lastIndex + index] = newsItem
                        _newsList.value = currentList.toMutableList()
                        loadingItemCount--
                    }
                    Unit
                }
                fetchJobs.add(fetchJob)
            }

            fetchJobs.awaitAll()
            lastIndex = endIndex
            isLoading.postValue(false)
            isLastPage = endIndex == storyIds.size
        }
    }

    private suspend fun fetchStoryWithContent(id: Long): Items? {
        return withContext(Dispatchers.IO) {
            try {
                val story = RetrofitInstance.api.getItem(id)
                story.context = ContentExtractor.fetchContent(story.url).take(300)
                story
            } catch (e: Exception) {
                Log.e("TopStoryViewModel", "Error fetching story content for ID: $id", e)
                null
            }
        }
    }

    fun refreshNews(storyType: StoryType) {
        lastIndex = 0
        isLastPage = false
        _newsList.postValue(mutableListOf())
        fetchStoryIds(storyType)
    }

}