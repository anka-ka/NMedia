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
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.github.javafaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    val data: LiveData<FeedModel> = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default)

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }


    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _postError = SingleLiveEvent<String>()
    val postError: LiveData<String>
        get() = _postError

    private val _shouldUpdate = MutableLiveData(false)
    val shouldUpdate: MutableLiveData<Boolean>
        get() = _shouldUpdate

    private val _newPostsAvailable = MutableLiveData<Boolean>()
    val newPostsAvailable: LiveData<Boolean>
        get() = _newPostsAvailable


    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.postValue(FeedModelState(loading = true))
            try {
                repository.getAll()
                _state.value = FeedModelState()
                checkForNewPosts()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }
    fun refreshPosts() {
        viewModelScope.launch {
            _state.postValue(FeedModelState(refreshing = true))
            _state.value = try {
                repository.getAllVisible()
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
    private suspend fun checkForNewPosts() {
        val lastKnownPostId = repository.getLastPostId()
        val newPostsCount = lastKnownPostId?.let { repository.getNewerCount(it).first() }
        if (newPostsCount != null) {
            _newPostsAvailable.postValue(newPostsCount > 0)
        }
    }

    fun getAllVisible() {
        viewModelScope.launch {
            try {
                repository.getAllVisible()
                _shouldUpdate.postValue(true)
                _newPostsAvailable.postValue(false)
            } catch (e: Exception) {
                    _state.postValue(FeedModelState(error = true))
                }
            }
    }
}
