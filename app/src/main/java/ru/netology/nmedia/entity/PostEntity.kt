package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.datatransferobjects.Post


@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val videoLink: String? = null,
    val authorAvatar: String,
    val hidden: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbedded?,
) {
    fun toDto() = Post(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likes = likes,
        shares = shares,
        likedByMe = likedByMe,
        videoLink = videoLink,
        attachment = attachment?.toDto(),
    )

    companion object {
        fun fromDto(dto: Post, hidden: Boolean = false) =
            PostEntity(
                dto.id,
                dto.author,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.shares,
                dto.videoLink,
                dto.authorAvatar,
                hidden,
                attachment = dto.attachment ?. let{
                    AttachmentEmbedded.fromDto(it)
                }
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(hidden: Boolean = false): List<PostEntity> = map { PostEntity.fromDto(it, hidden) }