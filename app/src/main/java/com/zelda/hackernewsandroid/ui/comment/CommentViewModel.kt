package com.zelda.hackernewsandroid.ui.comment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.Items
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewModel : ViewModel() {

    val storyDetails = MutableLiveData<Items>()
    val comments = MutableLiveData<List<Items>>()
    private val pageSize = 10

    fun fetchStoryDetails(itemId: Long, page: Int = 0) {
        viewModelScope.launch {
            try {
                val newsItem = RetrofitInstance.api.getItem(itemId)
                if (page == 0) {
                    storyDetails.postValue(newsItem)
                }

                newsItem.kids?.let { kids ->
                    val startIndex = page * pageSize
                    val endIndex = min(startIndex + pageSize, kids.size)

                    val currentKids = kids.subList(startIndex, endIndex)
                    val fetchedComments = mutableListOf<Items>()

                    val commentsDeferred = currentKids.map { kidId ->
                        async { fetchCommentDetails(kidId).await() }
                    }

                    fetchedComments.addAll(commentsDeferred.awaitAll().filterNotNull())

                    // Use postValue to append new comments to existing list
                    val existingComments = comments.value.orEmpty()
                    comments.postValue(existingComments + fetchedComments)
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Error fetching story details", e)
            }
        }
    }

    fun fetchCommentDetails(itemId: Long): Deferred<Items?> = viewModelScope.async {
        try {
            val comment = RetrofitInstance.api.getItem(itemId)
            val childCommentsDeferred: List<Deferred<Items?>> = comment.kids?.map { kidId ->
                async { fetchCommentDetails(kidId).await() }
            }.orEmpty()

            comment.childComments = childCommentsDeferred.awaitAll().filterNotNull()
            comment
        } catch (e: Exception) {
            Log.e("CommentViewModel", "Error fetching comment details", e)
            null
        }
    }

    fun loadNextPage(itemId: Long) {
        val currentPage = (comments.value?.size ?: 0) / pageSize
        fetchStoryDetails(itemId, currentPage + 1)
    }
}
