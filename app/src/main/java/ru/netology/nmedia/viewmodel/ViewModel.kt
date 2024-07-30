package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import android.content.Context
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.javafaker.Faker
import kotlinx.coroutines.launch
import ru.netology.nmedia.model.FeedModelState

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0
)



class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = with(AppDb.getInstance(context = application)) {
        PostRepositoryImpl(postDao(), application)
    }


    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state
    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(posts = it, empty = it.isEmpty())
    }
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _postError = SingleLiveEvent<String>()
    val postError: LiveData<String>
        get() = _postError


    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.postValue(FeedModelState(loading = true))
            _state.value = try {
                repository.getAll()
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }
    fun refreshPosts() {
        viewModelScope.launch {
            _state.postValue(FeedModelState(refreshing = true))
            _state.value = try {
                repository.getAll()
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }
    val edited = MutableLiveData(empty)

    fun changeContentAndSave(content: String) {

        viewModelScope.launch {
            val faker = Faker()
            try {
                edited.value?.let {
                    repository.save(it.copy(content = content, author = faker.name().fullName()))
                    _postCreated.postValue(Unit)
                }
                edited.value = empty
            } catch (e: Exception) {
                _postCreated.postValue(Unit)
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun onCloseEditClicked() {
        edited.value = empty

    }


    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }


        fun removeById(id: Long) {
            viewModelScope.launch {
                try {
                    repository.removeById(id)
                    FeedModelState()
                } catch (e: Exception) {
                    FeedModelState(error = true)
                }
            }
        }

     fun postError(error: String?) {
        error.let {
            _postError.postValue(it)
        }
    }
}
