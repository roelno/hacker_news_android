import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zelda.hackernewsandroid.Items
import com.zelda.hackernewsandroid.NewsRecyclerViewAdapter
import com.zelda.hackernewsandroid.R
import com.zelda.hackernewsandroid.StoriesViewModel
import com.zelda.hackernewsandroid.StoryType
import com.zelda.hackernewsandroid.databinding.FragmentBasestoryBinding

abstract class BaseStoryFragment(private val storyType: StoryType) : Fragment() {

    private var _binding: FragmentBasestoryBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding should not be accessed after onDestroyView or before onCreateView")

    private val sharedViewModel: StoriesViewModel by activityViewModels()
//    val sharedViewModel: StoriesViewModel by navGraphViewModels(R.id.navigation_top_stories)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_basestory, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel.fetchStoryIds(storyType)
//        sharedViewModel.refreshNews(storyType)
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
                        !sharedViewModel.isLoading.value!! &&
                        !sharedViewModel.isLastPage

                if (shouldLoadMore) {
                    sharedViewModel.loadMoreNews()
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
            sharedViewModel.refreshNews(storyType)
        }
    }


    private fun setupNewsListObserver() {
        sharedViewModel.newsList.observe(viewLifecycleOwner) { news ->
            val nonNullNewsList = news.filterNotNull()
            (binding.newsRecyclerView.adapter as? NewsRecyclerViewAdapter)?.updateNewsList(
                nonNullNewsList
            )
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupLoadingObserver() {
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    private fun setupErrorMessageObserver() {
        sharedViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
