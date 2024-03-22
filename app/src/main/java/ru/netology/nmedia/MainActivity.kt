package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    var shared: Boolean = false
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего.",
            content = "Привет! Это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до увереннных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше,бежать быстрее. Наша миссия - помочь встать на путь роста и начать цепочку перемен - http://netolo.gy/fyb ",
            published = "21 мая в 19:36",
            likedByMe = false,
            likes = 0,
            shares = 0,
            shared = false
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likes.text = post.likes.toString()
            if (post.likedByMe) {
                NumberOfLikes?.setImageResource(R.drawable.baseline_favorite_24)
            } else NumberOfLikes?.setImageResource(R.drawable.baseline_favorite_border_24)
            NumberOfLikes?.setOnClickListener {
                if (post.likedByMe) {
                    post.likes--
                } else {
                    post.likes++
                }
                post.likedByMe = !post.likedByMe
                likes.text = cutLongNumbers(post.likes)
                NumberOfLikes.setImageResource(
                    if (post.likedByMe) R.drawable.baseline_favorite_24
                    else R.drawable.baseline_favorite_border_24
                )
            }
            NumberOfShares?.setOnClickListener {
                if (post.shared) {
                    post.shares++
                }
                post.shared = !post.shared
                shares.text = cutLongNumbers(post.shares)
            }
        }
    }

    private fun cutLongNumbers(number: Int): String {
        return when {
            number < 1000 -> number.toString()
            number < 10_000 -> (number / 1000).toString() + "K"
            number < 1_000_000 -> String.format("%.1fK", number.toDouble() / 1000)
            else -> String.format("%.1fM", number.toDouble() / 1_000_000)
        }
    }
}
