package ru.netology.nmedia.datatransferobjects

data class Post(
    val id: Long,
    val author: String,
    var content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    val videoLink: String? = null,
    var shared: Boolean = false

)