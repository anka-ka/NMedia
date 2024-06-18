package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData

import ru.netology.nmedia.datatransferobjects.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(post: Post):Post
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post):Post
}