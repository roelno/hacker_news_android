package com.zelda.hackernewsandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zelda.hackernewsandroid.News
import com.zelda.hackernewsandroid.NewsRecyclerViewAdapter
import com.zelda.hackernewsandroid.api.RetrofitInstance
import com.zelda.hackernewsandroid.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Obtain ViewModel from the ViewModelProvider
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        // Set up the swipe refresh action
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.clearNewsList() // Clear current data
            homeViewModel.fetchTopNews()
        }

        // Observe the newsList LiveData
        homeViewModel.newsList.observe(viewLifecycleOwner) { news ->
            (binding.newsRecyclerView.adapter as? NewsRecyclerViewAdapter)?.updateNewsList(news)
            binding.swipeRefreshLayout.isRefreshing = false // Stop the refreshing indicator
        }

        // Trigger data fetch
        homeViewModel.fetchTopNews()
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