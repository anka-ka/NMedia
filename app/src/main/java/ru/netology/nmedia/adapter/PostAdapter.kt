package ru.netology.nmedia.adapter
import BASE_URL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.repository.PostRepositoryImpl

interface OnInteractionListener{
    fun onLike(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onShare(post: Post)
    fun onPlayVideo(post: Post)
    fun onOpenOnePost(post:Post)
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener

    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) = binding.apply {
        videoGroup.visibility = View.GONE
        if (post.videoLink != null) {
            videoGroup.visibility = View.VISIBLE
        }
        author.text = post.author
        published.text = post.published
        content.text = post.content
        shares.isChecked = post.shared
        shares.text = cutLongNumbers(post.shares)
        likes.isChecked = post.likedByMe
        likes.text = cutLongNumbers(post.likes)

        Glide.with(binding.root)
            .load("${BuildConfig.BASE_URL}avatars/${post.authorAvatar}")
            .placeholder(R.drawable.ic_loading_100dp)
            .error(R.drawable.baseline_error_24)
            .timeout(30_000)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(100)))
            .into(avatar)

        likes.setOnClickListener {
            onInteractionListener.onLike(post)
        }
        shares.setOnClickListener {
            onInteractionListener.onShare(post)
        }
        videoImage.setOnClickListener {
            onInteractionListener.onPlayVideo(post)
        }

        playVideo.setOnClickListener {
            onInteractionListener.onPlayVideo(post)
        }
        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.edit -> {
                            onInteractionListener.onEdit(post)
                            true
                        }
                        R.id.remove -> {
                            onInteractionListener.onRemove(post)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
        content.setOnClickListener {
            onInteractionListener.onOpenOnePost(post)
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
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