package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likes.text = viewModel.cutLongNumbers(post.likes)
                shares.text = viewModel.cutLongNumbers(post.shares)
                NumberOfLikes.setImageResource(
                    if (post.likedByMe) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
                )
                binding.NumberOfLikes.setOnClickListener {
                    viewModel.changeLikes()
                    viewModel.like()
                }

                binding.NumberOfShares.setOnClickListener {
                    viewModel.changeShares()
                    viewModel.share()
                }
            }
        }
    }
}
