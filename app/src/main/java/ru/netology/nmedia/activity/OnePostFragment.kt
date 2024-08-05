package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class OnePostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }
    private val viewModel: PostViewModel by activityViewModels()
//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment
//    )

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
        viewModel.data.observe(viewLifecycleOwner) { model ->
            val post = model.posts.find { it.id == arguments?.textArg?.toLong() } ?: return@observe

            val onInteractionListener = object : OnInteractionListener {
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
            }

            val viewHolder = PostViewHolder(binding, onInteractionListener::onImageClick, onInteractionListener)
            viewHolder.bind(post)
        }

        return binding.root
    }
}