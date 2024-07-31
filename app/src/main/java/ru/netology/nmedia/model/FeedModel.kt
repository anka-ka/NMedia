package ru.netology.nmedia.model

import ru.netology.nmedia.datatransferobjects.Post
import java.lang.Error

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,

    )