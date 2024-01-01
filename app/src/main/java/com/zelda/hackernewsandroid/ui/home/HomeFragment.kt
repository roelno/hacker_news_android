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
    private var newsList = mutableListOf<News>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fetchTopNews()
        return root
    }

    private fun fetchTopNews() {
        lifecycleScope.launch {
            try {
                val storyIds = RetrofitInstance.api.getTopStoryIds()
                storyIds.take(50) // Taking only the first 50 stories
                    .forEach { id ->
                        try {
                            val story = RetrofitInstance.api.getStory(id)
                            newsList.add(story)
                        } catch (e: Exception) {
                            // Handle exceptions for individual story fetch
                        }
                    }
                setupRecyclerView()
            } catch (e: Exception) {
                // Handle exceptions for fetching story IDs
            }
        }
    }


    private fun setupRecyclerView() {
        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NewsRecyclerViewAdapter(newsList)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}