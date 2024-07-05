package ru.netology.nmedia.repository


import ru.netology.nmedia.datatransferobjects.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun getAllAsync(callback: NMediaCallback<List<Post>>)
    fun likeById(post: Post,callback: NMediaCallback<Post>)
    fun shareById(id: Long)
    fun removeById(id: Long,callback: NMediaCallback<Unit>)
    fun save(post: Post, callback: NMediaCallback<Post>)

    interface NMediaCallback<T> {
        fun onSuccess(data: T)
        fun onError(e: Exception)
    }
}