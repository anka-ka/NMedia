package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.datatransferobjects.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val type = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()
        val response = client.newCall(request)
            .execute()
        val responseText = response.body?.string() ?: error("Response body is null")
        return gson.fromJson(responseText, type)
    }

    override fun likeById(post: Post): Post {
        val request = if (post.likedByMe) {
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/${post.id}/likes")
                .build()

        } else {
            Request.Builder()
                .url("${BASE_URL}/api/posts/${post.id}/likes")
                .post(gson.toJson(post, Post::class.java).toRequestBody(jsonType))
                .build()
        }
        val call = client.newCall(request)
        val response = call.execute()
        val bodyText = requireNotNull(response.body).string()
        return gson.fromJson(bodyText, Post::class.java)
    }

    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun save(post: Post): Post {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        val response = client.newCall(request)
            .execute()
        val responseText = response.body?.string() ?: error("Response body is null")
        return gson.fromJson(responseText, Post::class.java)
    }

    override fun removeById(id: Long) {
        val request = Request.Builder()
            .delete()
            .url("${BASE_URL}api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}