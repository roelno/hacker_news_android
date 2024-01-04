package com.zelda.hackernewsandroid.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        val commentKidsIDs = arguments?.getLong("kids")
//        viewModel.newsID.value = newsId.toString()

        viewModel.fetchItemDetails(newsId!!)
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.storyDetails.observe(viewLifecycleOwner) { story ->
            story?.let {
                binding.titleText.text = it.title ?: ""
                var pts = "${it.score?.toString() ?: "0"} pts"
                var cmts = "${it.kids?.size?.toString() ?: "0"} cmts"
                var postBy = "by ${it.by!!}"
                var time = "${getTimeAgo(it.time!!)}"
                binding.pointsCommentsPostByTimeText.text = pts + " | " + cmts + " | " + postBy +" | " + time
            }
        }
    }

    fun getTimeAgo(time: Long): String {
        val currentTime = System.currentTimeMillis() / 1000
        val diffInSeconds = currentTime - time

        val minutes = diffInSeconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
    }

}
