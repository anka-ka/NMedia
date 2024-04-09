package ru.netology.nmedia.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.datatransferobjects.Post


typealias OnLikeListener = (Post) -> Unit
typealias OnShareListener = (Post) -> Unit

class PostAdapter(
    private val likeListener: OnLikeListener,
    private val shareListener: OnShareListener
) : ListAdapter<Post, PostViewHolder>(PostDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, likeListener, shareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val likeListener: OnLikeListener,
    private val shareListener: OnShareListener,

    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) = binding.apply {
        author.text = post.author
        published.text = post.published
        content.text = post.content
        likes.text = cutLongNumbers(post.likes)
        shares.text = cutLongNumbers(post.shares)
        NumberOfLikes.setImageResource(
            if (post.likedByMe) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )
        NumberOfLikes.setOnClickListener {
            likeListener(post)

        }
        NumberOfShares.setOnClickListener {
            shareListener(post)
        }
    }
}

object PostDiffUtil : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}

fun cutLongNumbers(number: Int): String {
    val formattedNumber = when {
        number < 1000 -> number.toString()
        number < 10_000 -> "${number / 1000}.${(number % 1000) / 100}K"
        number in 10_000..1_000_000 -> (number / 1000).toString() + "K"
        else -> "${number / 1_000_000}.${number % 1_000_000 / 100_000}M"
    }
    return formattedNumber
}