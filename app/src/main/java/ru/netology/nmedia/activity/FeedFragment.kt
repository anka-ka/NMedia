package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class FeedFragment : Fragment() {
    private  val dependencyContainer = DependencyContainer.getInstance()
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
        factoryProducer = {
            ViewModelFactory(dependencyContainer.repository, dependencyContainer.appAuth)
        }
    )
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

        binding.refresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPost = state.posts.size > adapter.currentList.size
            binding.emptyState.isVisible = state.empty
            adapter.submitList(state.posts) {
                if (newPost) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }
        viewModel.newPostsAvailable.observe(viewLifecycleOwner) { hasNewPosts ->
            binding.buttonNew.isVisible = hasNewPosts
        }
        viewModel.newerCount.observe(viewLifecycleOwner) { count ->
            binding.buttonNew.isVisible = count > 0
            binding.buttonNew.setOnClickListener {
                viewModel.showAll()
                binding.buttonNew.isVisible = false
            }
        }
        viewModel.shouldUpdate.observe(viewLifecycleOwner) { shouldUpdate ->
            if (shouldUpdate) {
                viewModel.data.observe(viewLifecycleOwner) { feedModel ->
                    val newPostCount = feedModel.posts.size
                    binding.emptyState.isVisible = feedModel.empty
                    adapter.submitList(feedModel.posts) {
                        if (newPostCount > 0) {
                            binding.list.smoothScrollToPosition(0)
                        }
                    }
                }
                viewModel.shouldUpdate.value = false
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(
                    binding.root,
                    R.string.error_loading,
                    Snackbar.LENGTH_SHORT
                )
                    .setAction(R.string.retry_loading) {
                        viewModel.loadPosts()
                    }
                    .show()
            }
            binding.progress.isVisible = state.loading
            binding.refresh.isRefreshing = state.refreshing
        }

        viewModel.postError.observe(viewLifecycleOwner) { error ->
            Snackbar.make(
                requireView(),
                error,
                Snackbar.LENGTH_LONG
            ).setAction(R.string.retry_loading) {
                viewModel.loadPosts()
            }.show()
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


//       viewModel.data.observe(viewLifecycleOwner) { posts ->
//            val newPost = posts.size > adapter.currentList.size
//            adapter.submitList(posts) {
//                if (newPost) binding.list.smoothScrollToPosition(0)
//            }
//        }
//
//        binding.addNewPost.setOnClickListener {
//            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
//        }
//
//        viewModel.edited.observe(viewLifecycleOwner) { post ->
//            if (post.id == 0L) {
//                return@observe
//            }
//            findNavController().navigate(
//                R.id.action_feedFragment_to_newPostFragment,
//                Bundle().apply {
//                    textArg = post.content
//                }
//            )
//        }
//        return binding.root
//    }
//}
