package com.zelda.hackernewsandroid.ui.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentViewModel : ViewModel() {
    // LiveData for title
    val title = MutableLiveData<String>()
}
