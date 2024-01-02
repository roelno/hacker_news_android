package com.zelda.hackernewsandroid.ui.topstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zelda.hackernewsandroid.NewsRecyclerViewAdapter
import com.zelda.hackernewsandroid.databinding.FragmentTopStoryBinding

class TopStoryFragment : Fragment() {

    private var _binding: FragmentTopStoryBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Obtain ViewModel from the ViewModelProvider
        val topStoryViewModel = ViewModelProvider(this)[TopStoryViewModel::class.java]
        _binding = FragmentTopStoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        // Set up the swipe refresh action
        binding.swipeRefreshLayout.setOnRefreshListener {
            topStoryViewModel.clearNewsList() // Clear current data
            topStoryViewModel.fetchTopNews()
        }

        // Observe the newsList LiveData
        topStoryViewModel.newsList.observe(viewLifecycleOwner) { newsList ->
            val filteredList = newsList.filterNotNull()
            (binding.newsRecyclerView.adapter as? NewsRecyclerViewAdapter)?.updateNewsList(filteredList)
            if (filteredList.size == newsList.size) {
                binding.swipeRefreshLayout.isRefreshing = false // Stop the refreshing indicator when all items are loaded
            }
        }

        // Trigger data fetch
        topStoryViewModel.fetchTopNews()
        return root
    }

    private fun setupRecyclerView() {
        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                NewsRecyclerViewAdapter(mutableListOf()) // Initialize with a mutable empty list
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}