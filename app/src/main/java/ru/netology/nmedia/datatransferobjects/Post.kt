package ru.netology.nmedia.datatransferobjects

data class Post(
    val id: Long=0,
    val author: String= "",
    var content: String= "",
    val authorId: Long,
    val published: String= "",
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    val videoLink: String? = null,
    val authorAvatar: String = "",
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)