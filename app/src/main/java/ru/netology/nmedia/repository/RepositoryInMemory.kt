package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.datatransferobjects.Post

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
    fun share()
    fun cutLongNumbers()
    fun changeLikes()
    fun changeShares()
}
class RepositoryInMemory : PostRepository {
    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего.",
        content = "Привет! Это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до увереннных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше,бежать быстрее. Наша миссия - помочь встать на путь роста и начать цепочку перемен - http://netolo.gy/fyb ",
        published = "21 мая в 19:36",
        likedByMe = false,
        likes = 9999,
        shares = 0,
        shared = false
    )
    private val data = MutableLiveData(post)
    private val longNumber = MutableLiveData(post)
    override fun get(): LiveData<Post> = data
    override fun like() {
        post = post.copy(likedByMe = !post.likedByMe)
        data.value = post
    }
    override fun share(){
        post = post.copy(shared = !post.shared)
        data.value = post
    }
    override fun cutLongNumbers() {
        longNumber.value = post
        data.value = post
    }

    override fun changeLikes() {
           if (post.likedByMe) {
                post.likes--
            } else {
                post.likes++
            }

    }
    override fun changeShares(){
        post.shares++
    }
}