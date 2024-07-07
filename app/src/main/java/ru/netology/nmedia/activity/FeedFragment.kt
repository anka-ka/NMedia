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
import ru.netology.nmedia.viewmodel.PostViewModel




class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
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

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onShare(post: Post) {
                val  intent = Intent().apply{
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
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
        })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner){ model ->
            binding.errorGroup.isVisible = model.error
            binding.emptyState.isVisible = model.empty
            binding.progress.isVisible = model.loading
            adapter.submitList(model.posts)

        }
        viewModel.postError.observe(viewLifecycleOwner) { error ->
            Snackbar.make(
                requireView(),
                error,
                Snackbar.LENGTH_LONG
            ).show()
        }

        binding.retry.setOnClickListener{
            viewModel.loadPosts()
        }
        binding.addNewPost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
       }


        return binding.root
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
