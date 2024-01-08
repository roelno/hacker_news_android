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
import com.zelda.hackernewsandroid.Items
import com.zelda.hackernewsandroid.NewsRecyclerViewAdapter
import com.zelda.hackernewsandroid.R
import com.zelda.hackernewsandroid.databinding.FragmentTopStoryBinding

class StoryFragment : Fragment() {

    private var _binding: FragmentTopStoryBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding should not be accessed after onDestroyView or before onCreateView")

    private val storyViewModel: StoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_story, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = storyViewModel

        val storyTypeString = arguments?.getString("storyType") ?: "top"
        val storyType = when (storyTypeString) {
            "top" -> StoryType.TOP
            "best" -> StoryType.BEST
            "new" -> StoryType.NEW
            else -> StoryType.TOP
        }
        storyViewModel.setStoryType(storyType)

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
                        !storyViewModel.isLoading.value!! &&
                        !storyViewModel.isLastPage

                if (shouldLoadMore) {
                    storyViewModel.loadMoreNews()
                }
            }
        })
    }

    private fun onNewsItemClicked(news: Items) {
        val bundle = Bundle().apply {
            putString("title", news.title)
            putLong("id", news.id)
            putLongArray("kids", news.kids?.toLongArray())
        }
        findNavController().navigate(R.id.action_navigation_top_story_to_commentFragment, bundle)
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            storyViewModel.refreshNews()
        }
    }


    private fun setupNewsListObserver() {
        storyViewModel.newsList.observe(viewLifecycleOwner) { news ->
            val nonNullNewsList = news.filterNotNull()
            (binding.newsRecyclerView.adapter as? NewsRecyclerViewAdapter)?.updateNewsList(
                nonNullNewsList
            )
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupLoadingObserver() {
        storyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    private fun setupErrorMessageObserver() {
        storyViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
