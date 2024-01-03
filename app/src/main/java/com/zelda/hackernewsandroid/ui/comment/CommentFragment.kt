package com.zelda.hackernewsandroid.ui.comment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.zelda.hackernewsandroid.R
import com.zelda.hackernewsandroid.databinding.FragmentCommentBinding

class CommentFragment : Fragment() {
    private lateinit var viewModel: CommentViewModel
    private lateinit var binding: FragmentCommentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false)
        viewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Retrieve News id
        val newsId = arguments?.getLong("id")
        viewModel.newsID.value = newsId.toString()

        // load the News Comment page
        fetchNewsCommentDetails(newsId!!)

        return binding.root
    }

    private fun fetchNewsCommentDetails(itemId: Long) {
        viewModel.fetchItemDetails(itemId) // Implement this method in your ViewModel
    }
}
