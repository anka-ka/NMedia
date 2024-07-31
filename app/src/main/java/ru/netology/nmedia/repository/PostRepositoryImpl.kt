package ru.netology.nmedia.repository

import ApiService
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.R
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppUnknownError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val context: Context
) : PostRepository {
    override val data: LiveData<List<Post>> = postDao.getAll().map{
        it.map(PostEntity::toDto)
    }


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

    val post = postDao.getById(id)?: return
    postDao.likeById(id)

    try {
        val response = withContext(Dispatchers.IO) {
            if (post.likedByMe) {
                ApiService.service.dislikeById(id)
            } else {
                ApiService.service.likeById(id)
            }
        }

        if (!response.isSuccessful) {
            postDao.likeById(id)
            throw RuntimeException(context.getString(R.string.post_error))
        }

        val updatedPost = response.body() ?: throw RuntimeException(context.getString(R.string.response_error))

        postDao.insert(PostEntity.fromDto(updatedPost))
    } catch (e: IOException) {
        postDao.likeById(id)
        throw NetworkError
    } catch (e: Exception) {
        postDao.likeById(id)
        throw AppUnknownError
    }
}


    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun save(post: Post) {
        try {
            val response = ApiService.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw AppUnknownError
        }
    }

override suspend fun removeById(id: Long) {
    val postEntity = postDao.getById(id)
    postDao.removeById(id)

    try {
        val response = ApiService.service.removeById(id)
        if (!response.isSuccessful) {
            if (postEntity != null) {
                postDao.insert(postEntity)
            }
            throw ApiError(response.code(), context.getString(R.string.post_error))
        }
    } catch (e: IOException) {
        if (postEntity != null) {
            postDao.insert(postEntity)
        }
        throw NetworkError
    } catch (e: Exception) {
        if (postEntity != null) {
            postDao.insert(postEntity)
        }
        throw AppUnknownError
    }
}
}