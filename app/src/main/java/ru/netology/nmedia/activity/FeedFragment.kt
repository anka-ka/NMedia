package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostAdapter(
            object : OnInteractionListener {
                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.content)
                    }
                    val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }

                override fun onPlayVideo(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(post.videoLink)
                    }
                    val playVideoIntent = Intent.createChooser(intent, "video")
                    startActivity(playVideoIntent)
                }

                override fun onOpenOnePost(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_onePostFragment,
                        Bundle().apply {
                            textArg = post.id.toString()
                        })
                }

                override fun onImageClick(imageUrl: String) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_imageFullScreenFragment,
                        Bundle().apply {
                            putString("imageUrl", imageUrl)
                        }
                    )
                }
            }
        ) { imageUrl ->
            onImageClick(imageUrl)
        }

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter{
                adapter.retry()
            },
            footer = PostLoadingStateAdapter{
                adapter.retry()
            }
        )
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadStates ->
                binding.progress.isVisible = loadStates.refresh is LoadState.Loading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest { pagingData ->
                adapter.submitData(pagingData)
                binding.emptyState.isVisible = adapter.itemCount == 0
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newerCount.collectLatest { count ->
                binding.buttonNew.isVisible = count > 0
                binding.buttonNew.setOnClickListener {
                    viewModel.showAll()
                    binding.buttonNew.isVisible = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { state ->
                binding.refresh.isRefreshing = state.refresh is LoadState.Loading
            }
        }

        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }

        binding.addNewPost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }

    private fun onImageClick(url: String) {
        findNavController().navigate(
            R.id.action_feedFragment_to_imageFullScreenFragment,
            Bundle().apply {
                putString("imageUrl", url)
            }
        )
    }
}
