package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl
import ru.netology.nmedia.repository.PostRepositorySharedPrefsImpl
import ru.netology.nmedia.repository.RepositoryInMemory

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

    private val repository: PostRepository = PostRepositoryFileImpl(application)

    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun edit (post: Post){
        edited.value = post
    }
    fun onCloseEditClicked() {
        edited.value = empty

    }
    fun changeContentAndSave(content: String) {
        val text = content.trim()
        edited.value?.let{
            if(it.content != text.trim()){
                repository.save(it.copy(content = text))
            }
        }
        edited.value = empty
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}
