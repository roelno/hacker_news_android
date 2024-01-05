package com.zelda.hackernewsandroid.ui.comment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.Items
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewModel : ViewModel() {

//    val newsID = MutableLiveData<String>() // retrieved from TopStoryFragment

    val storyDetails = MutableLiveData<Items>()





    fun fetchItemDetails(itemId: Long) {
        viewModelScope.launch {
            try {
                val newsItem = RetrofitInstance.api.getStory(itemId)
                storyDetails.postValue(newsItem)
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Error fetching item details", e)
                // Handle error
            }
        }
    }

    fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(time * 1000))
    }


}
