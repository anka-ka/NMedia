package ru.netology.nmedia.repository


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.datatransferobjects.MediaUpload
import ru.netology.nmedia.datatransferobjects.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun showAll()
    suspend fun getAll()
    fun getNewerCount(id: Long?): Flow<Int>
    suspend fun getAllVisible()
    suspend fun getLastPostId(): Long?
    fun getHiddenCount(): Flow<Int>
    fun refreshPosts()
    suspend fun likeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, photo: MediaUpload)


}