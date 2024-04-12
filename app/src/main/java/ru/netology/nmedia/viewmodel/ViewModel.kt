package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.repository.PostRepository
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


class PostViewModel : ViewModel() {

    private val repository: PostRepository = RepositoryInMemory()

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
