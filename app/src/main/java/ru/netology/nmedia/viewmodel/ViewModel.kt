package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0,
    shared = false
)



class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.NMediaCallback<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let { post ->
            repository.save(post, object : PostRepository.NMediaCallback<Post> {
                override fun onSuccess(data: List<Post> ) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    edited.postValue(empty)
                }
            })
        }
        edited.postValue(empty)
    }


    fun edit(post: Post) {
        edited.value = post
    }
    fun onCloseEditClicked() {
        edited.value = empty

    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
    fun likeById(post: Post) {
        repository.likeById(post, object : PostRepository.NMediaCallback<Post> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(_data.value?.copy(
                    posts = _data.value?.posts.orEmpty().map {
                        if (it.id == post.id) it.copy(likedByMe = true) else it
                    }
                ))
            }

            override fun onError(e: Exception) {
                _data.value
            }
        })
        loadPosts()
    }

    fun removeById(id: Long) {
        repository.removeById(id, object : PostRepository.NMediaCallback<Post> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(
                    _data.value?.copy
                        (posts = _data.value?.posts.orEmpty().filter {
                        it.id != id
                    })
                )
            }

            override fun onError(e: Exception) {
                _data.value
            }
        }
        )
        loadPosts()
    }
}
