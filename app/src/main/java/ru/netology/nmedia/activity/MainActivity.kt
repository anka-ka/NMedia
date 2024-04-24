package ru.netology.nmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import ru.netology.nmedia.activity.NewPostContract
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.PostViewModel




class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val newPostLauncher = registerForActivityResult(NewPostContract){
            val result = it ?:return@registerForActivityResult
            viewModel.changeContentAndSave(result)
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
//        binding.close.setOnClickListener {
//            with(binding.content) {
//                viewModel.onCloseEditClicked()
//                clearFocus()
//                AndroidUtils.hideKeyboard(binding.content)
//            }
//            binding.group.visibility = View.GONE
//        }
//        viewModel.edited.observe(this) { thisPost ->
//            with(binding.content) {
//                if (thisPost?.id != 0L) {
//                    val content = thisPost.content
//                    setText(content)
//                    binding.group.visibility = View.VISIBLE
//                    binding.secondContent.text = content
//                    focusAndShowKeyboard()
//                } else {
//                    binding.group.visibility = View.GONE
//                    binding.content.setText("")
//                    clearFocus()
//                    AndroidUtils.hideKeyboard(binding.content)
//                }
//            }
//        }

        binding.addNewPost?.setOnClickListener {
           newPostLauncher.launch()
        }
    }
}
