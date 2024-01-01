package com.zelda.hackernewsandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var newsList = listOf<News>()

//    // sample data
//    var newsList = listOf<News>(
//        News(
//            "Bazzite – a SteamOS-like OCI image for desktop, living room, and handheld PCs",
//            "Bazzite is an OCI image that serves as an alternative operating system for the Steam Deck, and a ready-to-game SteamOS-like for desktop computers and living room home theater PCs.\n" +
//                    "Bazzite is built from ublue-os/main and ublue-os/nvidia using Fedora technology, which means expanded hardware support and built in drivers are included. Additionally, Bazzite adds the following features...",
//            "\"https://hacker-news.firebaseio.com/v0/"
//        ),
//        News(
//            "Show HN: Pokemon prototype game made with JavaScript and p5.js ",
//            "Pokemon prototype game made with JavaScript + p5.js Live demo : https://jslegend.itch.io/p5-pokemon-prototype ...\n",
//            "\"https://hacker-news.firebaseio.com/v0/"
//        )
//    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        fetchNews()

        return root
    }

    private fun setupRecyclerView() {
        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NewsRecyclerViewAdapter(newsList)
        }
    }

    private fun fetchNews() {
        lifecycleScope.launch {
            try {
                // Example IDs of stories, you should fetch these dynamically.
                val storyIds = listOf(9129911, 9129199, 9127761, 9128141, 9128264, 9127792, 9129248, 9127092, 9128367) // Sample story IDs
                newsList = storyIds.mapNotNull { id ->
                    try {
                        RetrofitInstance.api.getStory(id)
                    } catch (e: Exception) {
                        null
                    }
                }
                setupRecyclerView()
            } catch (e: Exception) {
                // Handle exceptions (e.g., show an error message)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}