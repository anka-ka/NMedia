package ru.netology.nmedia.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.datatransferobjects.Post
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia.repository.PostRepository
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.datatransferobjects.MediaUpload
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.PostsApiService

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0,
    authorId = 0,
)


@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private  val repository: PostRepository,
     appAuth: AppAuth,
    private val service: PostsApiService

    ) : ViewModel() {



    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state


    val data: Flow<PagingData<Post>> = appAuth.data
        .flatMapLatest { token ->
            val myId = token?.id

            repository.data
                .map { posts ->
                        posts.map {
                            it.copy(ownedByMe = it.authorId == myId)}

                }
        }.flowOn(Dispatchers.Default)


    val newerCount: Flow<Int> = appAuth.data
        .flatMapLatest { token ->
            val postId = token?.id
            repository.getNewerCount(postId)
                .catch { e -> e.printStackTrace() }

        }.flowOn(Dispatchers.Default)



    private val noPhoto = PhotoModel()

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _postError = SingleLiveEvent<String>()
    val postError: LiveData<String>
        get() = _postError

    private val _shouldUpdate = MutableStateFlow(false)
    val shouldUpdate: StateFlow<Boolean>
        get() = _shouldUpdate

    private val _newPostsAvailable = MutableStateFlow<Boolean>(false)
    val newPostsAvailable: StateFlow<Boolean>
        get() = _newPostsAvailable

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    val pagingData: Flow<PagingData<Post>> = repository.data


    init {
        appAuth.data
            .onEach { token ->
                repository.refreshPosts()
            }
            .launchIn(viewModelScope)
    }

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
    fun setPhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
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
        val text = content.trim()

        if (edited.value?.content != text) {
            edited.value = edited.value?.copy(content = text)
        }

        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(post)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(post, MediaUpload(file))
                        }
                    }
                    _state.value = FeedModelState()
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }
    fun showAll() = viewModelScope.launch {
        repository.showAll()
    }

    fun edit(post: Post) {
        edited.value = post
    }
    fun clearPhoto(){
        _photo.value = null
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
            _newPostsAvailable.value = newPostsCount > 0
        }
    }

    fun getAllVisible() {
        viewModelScope.launch {
            try {
                repository.getAllVisible()
                _shouldUpdate.value = true
                _newPostsAvailable.value = false
            } catch (e: Exception) {
                    _state.postValue(FeedModelState(error = true))
                }
            }
    }
    fun resetShouldUpdate() {
        _shouldUpdate.value = false
    }
}
