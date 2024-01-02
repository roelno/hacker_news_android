package com.zelda.hackernewsandroid.ui.topstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zelda.hackernewsandroid.NewsRecyclerViewAdapter
import com.zelda.hackernewsandroid.databinding.FragmentTopStoryBinding

class TopStoryFragment : Fragment() {

    private var _binding: FragmentTopStoryBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.
    private val topStoryViewModel: TopStoryViewModel by viewModels() // Initialize ViewModel using the 'viewModels' delegate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
//        // Obtain ViewModel from the ViewModelProvider
//        val topStoryViewModel = ViewModelProvider(this)[TopStoryViewModel::class.java]
        _binding = FragmentTopStoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        // Set up the swipe refresh action
        binding.swipeRefreshLayout.setOnRefreshListener {
//            topStoryViewModel.clearNewsList()
//            topStoryViewModel.fetchTopNews()
            topStoryViewModel.refreshNews()
        }

        // Observe the newsList LiveData
        topStoryViewModel.newsList.observe(viewLifecycleOwner) { news ->
            val nonNullNewsList = news.filterNotNull()
            (binding.newsRecyclerView.adapter as? NewsRecyclerViewAdapter)?.updateNewsList(nonNullNewsList)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return root
    }


    private fun setupRecyclerView() {
        val adapter = NewsRecyclerViewAdapter(mutableListOf())
        binding.newsRecyclerView.adapter = adapter
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!topStoryViewModel.isLoading && !topStoryViewModel.isLastPage) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                        topStoryViewModel.loadMoreNews()
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}