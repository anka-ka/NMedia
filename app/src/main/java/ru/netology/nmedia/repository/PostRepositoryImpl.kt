package ru.netology.nmedia.repository

import ApiService
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.netology.nmedia.R
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.AppUnknownError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val context: Context
) : PostRepository {
    override val data: Flow<List<Post>> = postDao.getAllVisible().map{
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
    override suspend fun showAll() = postDao.showAll()


    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }
    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10.seconds)
            val response = ApiService.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(hidden = true))
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override fun getHiddenCount(): Flow<Int> {
        return postDao.getHiddenCount()
    }

    override suspend fun getAllVisible() {
        try {
            val response = ApiService.service.getAll()
            if (!response.isSuccessful) {
                throw RuntimeException(
                    context.getString(
                        R.string.response_error
                    )
                )
            }
            val posts = response.body() ?: emptyList()
            postDao.insert(posts.map { PostEntity.fromDto(it, hidden = false) })

        } catch (e: Exception) {
            throw RuntimeException(context.getString(R.string.post_error))
        }
    }

    override suspend fun getLastPostId(): Long? {
        return postDao.getLastPostId().firstOrNull()
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