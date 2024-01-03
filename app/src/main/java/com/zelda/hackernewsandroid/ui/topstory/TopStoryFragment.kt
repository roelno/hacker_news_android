package com.zelda.hackernewsandroid.ui.topstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zelda.hackernewsandroid.News
import com.zelda.hackernewsandroid.NewsRecyclerViewAdapter
import com.zelda.hackernewsandroid.R
import com.zelda.hackernewsandroid.databinding.FragmentTopStoryBinding

class TopStoryFragment : Fragment() {

    private var _binding: FragmentTopStoryBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding should not be accessed after onDestroyView or before onCreateView")

    private val topStoryViewModel: TopStoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_story, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = topStoryViewModel

        setupRecyclerView()
        setupSwipeRefreshLayout()
        setupNewsListObserver()
        setupErrorMessageObserver()
        setupLoadingObserver()

        return binding.root
    }

    private fun setupRecyclerView() {
        val adapter = NewsRecyclerViewAdapter(mutableListOf(), this::onNewsItemClicked)
        binding.newsRecyclerView.adapter = adapter
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val shouldLoadMore = lastVisibleItemPosition == totalItemCount - 1 &&
                        !topStoryViewModel.isLoading.value!! &&
                        !topStoryViewModel.isLastPage

                if (shouldLoadMore) {
                    topStoryViewModel.loadMoreNews()
                }
            }
        })
    }

    private fun onNewsItemClicked(news: News) {
        val bundle = Bundle().apply {
            putString("title", news.title)
            putLong("id", news.id)
        }
        findNavController().navigate(R.id.action_navigation_top_story_to_commentFragment, bundle)
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            topStoryViewModel.refreshNews()
        }
    }


    private fun setupNewsListObserver() {
        topStoryViewModel.newsList.observe(viewLifecycleOwner) { news ->
            val nonNullNewsList = news.filterNotNull()
            (binding.newsRecyclerView.adapter as? NewsRecyclerViewAdapter)?.updateNewsList(
                nonNullNewsList
            )
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupLoadingObserver() {
        topStoryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    private fun setupErrorMessageObserver() {
        topStoryViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
