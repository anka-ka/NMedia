package ru.netology.nmedia.repository

import ApiService
import android.content.Context
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.combine
import ru.netology.nmedia.R
import ru.netology.nmedia.dao.DraftDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.entity.DraftEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppUnknownError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val draftDao: DraftDao,
    private val context: Context
) : PostRepository {
    override val data = draftDao.getAll()
        .combine(postDao.getAll()) { drafts, posts ->
            drafts.map { it.toDto() } + posts.map { it.toDto() }
        }
        .asLiveData()


    override suspend fun getAll() {
        val response = ApiService.service.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException(
                context.getString(
                    R.string.post_error
                )
            )
        }
        val posts = response.body() ?: throw RuntimeException(
            context.getString(
                R.string.response_error
            )
        )
        postDao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun likeById(id: Long) {
        val entity = postDao.likeById(id)
        val updated = entity.copy(
            likedByMe = !entity.likedByMe,
            likes = if (entity.likedByMe) entity.likes - 1 else entity.likes + 1
        )
        postDao.insert(updated)
        try {
            val response = if (entity.likedByMe) {
                ApiService.service.dislikeById(id)
            } else {
                ApiService.service.likeById(id)
            }
            if (!response.isSuccessful) {
                throw RuntimeException(context.getString(
                    R.string.post_error
                ))
            }
            val body = response.body() ?: throw RuntimeException(context.getString(
                R.string.response_error
            ))
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            postDao.insert(entity)
            throw NetworkError
        } catch (e: Exception) {
            postDao.insert(entity)
            throw AppUnknownError
        }
    }


    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun save(post: Post) {
        try {
            val id = draftDao.insert(DraftEntity(content = post.content))
            val response = ApiService.service.save(post)
            if (!response.isSuccessful) {
                throw RuntimeException(context.getString(
                    R.string.post_error
                ))
            }
            val body = response.body() ?: throw RuntimeException(context.getString(
                R.string.response_error
            ))
            draftDao.removeById(id)
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw AppUnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        postDao.removeById(id)
        try {
            val response = ApiService.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), context.getString(R.string.post_error))
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw AppUnknownError
        }
    }
}