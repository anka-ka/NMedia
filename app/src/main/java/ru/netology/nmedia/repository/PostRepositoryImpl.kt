package ru.netology.nmedia.repository

import ApiService

import retrofit2.Call
import retrofit2.Callback
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import ru.netology.nmedia.datatransferobjects.Post
import java.io.IOException

class PostRepositoryImpl: PostRepository {


    override fun getAll(): List<Post> {
        return ApiService.service.getAll()
            .execute()
            .let { it.body() ?: throw RuntimeException("body is null") }

    }
    override fun getAllAsync(callback: PostRepository.NMediaCallback<List<Post>>) {
        ApiService.service
            .getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }

                    val body: List<Post> = response.body() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(body)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))

                }
            })
    }

    override fun likeById(
        post: Post,
        callback: PostRepository.NMediaCallback<Post>
    ) {
        if (post.likedByMe) {
            ApiService.service.dislikeById(post.id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        val body: Post = response.body() ?: throw RuntimeException("body is null")

                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(Exception(t))
                    }
                })
        } else {
            ApiService.service.likeById(post.id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        val body: Post = response.body() ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(Exception(t))
                    }
                })
        }
    }


    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun save(post: Post, callback: PostRepository.NMediaCallback<Post>) {
        ApiService.service.save(post)
            .enqueue(
                object : Callback<Post> {
                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(Exception(t))
                    }

                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        val body: Post = response.body()
                            ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                })
    }

    override fun removeById(id: Long, callback: PostRepository.NMediaCallback<Unit>) {
            ApiService.service.removeById(id)
            .enqueue(
                object : Callback<Unit> {
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        callback.onError(Exception(t))
                    }

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        try {
                            callback.onSuccess(Unit)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )
    }
}