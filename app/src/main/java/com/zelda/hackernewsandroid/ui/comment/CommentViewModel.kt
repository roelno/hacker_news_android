package com.zelda.hackernewsandroid.ui.comment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelda.hackernewsandroid.api.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewModel : ViewModel() {

    val newsID = MutableLiveData<String>()

    val title = MutableLiveData<String>()
    val url = MutableLiveData<String?>()
    val score = MutableLiveData<Int?>()
    val by = MutableLiveData<String>()
    val time = MutableLiveData<Long>()

    fun fetchItemDetails(itemId: Long) {
        viewModelScope.launch {
            try {
                val itemDetails = RetrofitInstance.api.getStory(itemId)
                title.postValue(itemDetails.title!!)
                url.postValue(itemDetails.url)
                score.postValue(itemDetails.score)
                by.postValue(itemDetails.by!!)
                time.postValue(itemDetails.time!!)
                // Update other fields as necessary
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
