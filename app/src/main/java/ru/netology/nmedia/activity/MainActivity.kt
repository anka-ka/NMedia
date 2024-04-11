package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast

import androidx.activity.viewModels
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

        val adapter = PostAdapter(object : OnInteractionListener{
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
                viewModel.shareById(post.id)
            }
        })
        binding.list?.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = posts.size>adapter.currentList.size
            adapter.submitList(posts){
                if (newPost) {
                    binding.list?.smoothScrollToPosition(0)
                }
            }
        }
        viewModel.edited.observe(this) {
            if(it.id!= 0L){
                binding.content.setText(it.content)
                binding.content.focusAndShowKeyboard()
            }

        }
        binding.save?.setOnClickListener {
            val content = binding.content.text.toString()
            if (content.isBlank()) {
                Toast.makeText(this, R.string.error_empty_content, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.changeContentAndSave(content)
            binding.content.setText("")
            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(binding.content)
        }
    }
}
