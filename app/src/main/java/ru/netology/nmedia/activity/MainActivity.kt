package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { posts ->
            posts.map { post ->
                binding.container?.removeAllViews()
                CardPostBinding.inflate(layoutInflater, binding.container, false).apply {
                    author.text = post.author
                    published.text = post.published
                    content.text = post.content
                    NumberOfLikes.setImageResource(
                        if (post.likedByMe) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
                )
                    NumberOfLikes.setOnClickListener{
                        viewModel.likeById(post.id)
                    }
                }
            }
        }
    }
}
//            with(binding) {
//                author.text = post.author
//                published.text = post.published
//                content.text = post.content
//                likes.text = viewModel.cutLongNumbers(post.likes)
//                shares.text = viewModel.cutLongNumbers(post.shares)
//                NumberOfLikes.setImageResource(
//                    if (post.likedByMe) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
//                )
//                binding.NumberOfLikes.setOnClickListener {
//                    if (post.likedByMe) {
//                        post.likes--
//                    } else {
//                        post.likes++
//                    }
//                    viewModel.like()
//                    likes.text = viewModel.cutLongNumbers(post.likes)
//                }
//
//                binding.NumberOfShares.setOnClickListener {
//                    post.shares++
//                    viewModel.share()
//                    shares.text = viewModel.cutLongNumbers(post.shares)
//                }
//            }
//        }
//    }
//}
