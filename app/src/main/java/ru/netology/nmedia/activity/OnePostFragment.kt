package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class OnePostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels(

    )

    private lateinit var adapter: PostAdapter

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )

        if (adapter == null) {
            adapter = PostAdapter(
                onInteractionListener = object : OnInteractionListener {
                    override fun onEdit(post: Post) {
                        findNavController().navigate(
                            R.id.action_onePostFragment_to_newPostFragment,
                            Bundle().apply {
                                textArg = post.content
                            }
                        )
                        viewModel.edit(post)
                    }

                    override fun onRemove(post: Post) {
                        findNavController().navigateUp()
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
                    }

                    override fun onImageClick(imageUrl: String) {
                        findNavController().navigate(
                            R.id.action_onePostFragment_to_imageFullScreenFragment,
                            Bundle().apply {
                                putString("imageUrl", imageUrl)
                            }
                        )
                    }
                },
                        onImageClick = { imageUrl ->
                    findNavController().navigate(
                        R.id.action_onePostFragment_to_imageFullScreenFragment,
                        Bundle().apply {
                            putString("imageUrl", imageUrl)
                        }
                    )
                }
            )
        }

        binding.postCard.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest { pagingData ->
                adapter?.submitData(pagingData)

                val postId = arguments?.textArg?.toLongOrNull()
                val post = adapter?.snapshot()?.items?.find { it.id == postId }
            }
        }

        return binding.root
    }
}