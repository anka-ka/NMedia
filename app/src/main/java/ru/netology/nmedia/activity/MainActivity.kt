package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.activity.NewPostContract
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.viewmodel.PostViewModel




class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val newPostLauncher = registerForActivityResult(NewPostContract) { result ->
            if (result == null) {
                viewModel.onCloseEditClicked()
                return@registerForActivityResult
            } else {
                viewModel.changeContentAndSave(result)
                viewModel.onCloseEditClicked()
            }
        }

        val adapter = PostAdapter(object : OnInteractionListener {
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
        })

        binding.list?.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = posts.size > adapter.currentList.size
            adapter.submitList(posts) {
                if (newPost) {
                    binding.list?.smoothScrollToPosition(0)
                }
            }
        }
        viewModel.edited.observe(this) { thisPost ->
            if (thisPost?.id != 0L) {
                newPostLauncher.launch(thisPost.content)
            }
        }

        binding.addNewPost?.setOnClickListener {
            newPostLauncher.launch(null)
        }
    }
}
