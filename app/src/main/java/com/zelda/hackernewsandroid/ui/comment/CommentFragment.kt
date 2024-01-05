package com.zelda.hackernewsandroid.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zelda.hackernewsandroid.R
import com.zelda.hackernewsandroid.databinding.FragmentCommentBinding

class CommentFragment : Fragment() {
    private lateinit var viewModel: CommentViewModel
    private lateinit var binding: FragmentCommentBinding
    private var newsId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false)
        viewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        newsId = arguments?.getLong("id")!!
        viewModel.fetchStoryDetails(newsId!!)
        setupSwipeRefreshLayout()
        setupRecyclerView()
        setupHeaderObservers()
        setupCommentsObservers()

        return binding.root
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchStoryDetails(newsId)
        }
    }


    private fun setupRecyclerView() {
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.commentsRecyclerView.adapter = CommentsAdapter(mutableListOf())
        binding.commentsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) { // Check if we've reached the bottom
                    viewModel.loadNextPage(newsId)
                }
            }
        })
    }

    private fun setupHeaderObservers() {
        viewModel.storyDetails.observe(viewLifecycleOwner) { story ->
            story?.let {
                binding.titleText.text = it.title ?: ""
                var pts = "${it.score?.toString() ?: "0"} pts"
                var cmts = "${it.kids?.size?.toString() ?: "0"} cmts"
                var postBy = "by ${it.by!!}"
                var time = "${getTimeAgo(it.time!!)}"
                binding.pointsCommentsPostByTimeText.text = pts + " | " + cmts + " | " + postBy +" | " + time
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupCommentsObservers() {
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            (binding.commentsRecyclerView.adapter as CommentsAdapter).updateComments(comments)
        }
        binding.swipeRefreshLayout.isRefreshing = false

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.commentsLoadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
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
