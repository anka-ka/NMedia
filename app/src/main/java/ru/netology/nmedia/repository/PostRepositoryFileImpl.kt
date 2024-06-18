//package ru.netology.nmedia.repository
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import ru.netology.nmedia.datatransferobjects.ru.netology.nmedia.service.Post
//
//
//class PostRepositoryFileImpl(
//    private val context: Context,
//) : PostRepository {
//
//    companion object {
//        private const val FILENAME = "posts.json"
//    }
//    private val gson = Gson()
//    private val typeToken = TypeToken.getParameterized(List::class.java, ru.netology.nmedia.service.Post::class.java).type
//    private var nextId = 1L
//    private var posts = emptyList<ru.netology.nmedia.service.Post>()
//        private set(value){
//            field = value
//            sync()
//        }
//    private val data = MutableLiveData(posts)
//    private val longNumber = MutableLiveData(posts)
//    private var defaultPosts = listOf(
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Освоение новой профессии — это не только открывающиеся возможности и перспективы, но и настоящий вызов самому себе. Приходится выходить из зоны комфорта и перестраивать привычный образ жизни: менять распорядок дня, искать время для занятий, быть готовым к возможным неудачам в начале пути. В блоге рассказали, как избежать стресса на курсах профпереподготовки → http://netolo.gy/fPD",
//            published = "23 сентября в 10:12",
//            likedByMe = false,
//            likes = 9999,
//            shares = 0,
//            shared = false
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Делиться впечатлениями о любимых фильмах легко, а что если рассказать так, чтобы все заскучали \uD83D\uDE34\n",
//            published = "22 сентября в 10:14",
//            likedByMe = false,
//            likes = 0,
//            shares = 0,
//            shared = false
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Таймбоксинг — отличный способ навести порядок в своём календаре и разобраться с делами, которые долго откладывали на потом. Его главный принцип — на каждое дело заранее выделяется определённый отрезок времени. В это время вы работаете только над одной задачей, не переключаясь на другие. Собрали советы, которые помогут внедрить таймбоксинг \uD83D\uDC47\uD83C\uDFFB",
//            published = "22 сентября в 10:12",
//            likedByMe = false,
//            likes = 0,
//            shares = 0,
//            shared = false
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "\uD83D\uDE80 24 сентября стартует новый поток бесплатного курса «Диджитал-старт: первый шаг к востребованной профессии» — за две недели вы попробуете себя в разных профессиях и определите, что подходит именно вам → http://netolo.gy/fQ",
//            published = "21 сентября в 10:12",
//            likedByMe = false,
//            likes = 0,
//            shares = 0,
//            shared = false,
//            videoLink ="https://www.youtube.com/watch?v=1L-5DhhjXEo&ab_channel=GoldenSunset"
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Диджитал давно стал частью нашей жизни: мы общаемся в социальных сетях и мессенджерах, заказываем еду, такси и оплачиваем счета через приложения.",
//            published = "20 сентября в 10:14",
//            likedByMe = false,
//            likes = 3,
//            shares = 0,
//            shared = false,
//            videoLink ="https://www.youtube.com/watch?v=o1K9MxMkHc8&ab_channel=BeautifulMusicofNature"
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Большая афиша мероприятий осени: конференции, выставки и хакатоны для жителей Москвы, Ульяновска и Новосибирска \uD83D\uDE09",
//            published = "19 сентября в 14:12",
//            likedByMe = false,
//            likes = 0,
//            shares = 0,
//            shared = false
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Языков программирования много, и выбрать какой-то один бывает нелегко. Собрали подборку статей, которая поможет вам начать, если вы остановили свой выбор на JavaScript.",
//            published = "19 сентября в 10:24",
//            likedByMe = false,
//            likes = 0,
//            shares = 0,
//            shared = false,
//            videoLink = "https://www.youtube.com/watch?v=515an742WVg&ab_channel=%D0%9F%D0%BE%D0%BB%D1%83%D0%BE%D1%81%D1%82%D1%80%D0%BE%D0%B2%D0%A1%D0%BE%D0%BA%D1%80%D0%BE%D0%B2%D0%B8%D1%89"
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений, учимся рассказывать истории и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
//            published = "18 сентября в 10:12",
//            likedByMe = false,
//            likes = 9,
//            shares = 0,
//            shared = false,
//            videoLink = "https://www.youtube.com/watch?v=lyh2kAjcmSY&ab_channel=ScenicRelaxation"
//        ),
//        ru.netology.nmedia.service.Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего.",
//            content = "Привет! Это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до увереннных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше,бежать быстрее. Наша миссия - помочь встать на путь роста и начать цепочку перемен - http://netolo.gy/fyb ",
//            published = "21 мая в 19:36",
//            likedByMe = false,
//            likes = 9999,
//            shares = 0,
//            shared = false
//        )).reversed()
//
//    private fun sync() {
//        context.openFileOutput(FILENAME, Context.MODE_PRIVATE).bufferedWriter().use{
//            it.write(gson.toJson(posts))
//        }
//    }
//
//    init {
//        val file = context.filesDir.resolve(FILENAME)
//        if (file.exists()) {
//            context.openFileInput(FILENAME).bufferedReader().use {
//                posts = gson.fromJson(it, typeToken)
//                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
//            }
//        } else{
//
//            posts = defaultPosts
//        }
//        data.value = posts
//    }
//
//
//
//    override fun getAll(): LiveData<List<ru.netology.nmedia.service.Post>> = data
//    override fun save(post: ru.netology.nmedia.service.Post) {
//        if (post.id == 0L) {
//            posts = listOf(
//                post.copy(
//                    id = nextId++,
//                    author = "Me",
//                    likedByMe = false,
//                    published = "now"
//                )
//            ) + posts
//            data.value = posts
//            return
//        }
//
//        posts = posts.map {
//            if (it.id != post.id) it else it.copy(content = post.content)
//        }
//        data.value = posts
//    }
//    override fun likeById(id: Long) {
//        posts = posts.map {
//            if (it.id != id) it else it.copy(
//                likedByMe = !it.likedByMe,
//                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
//            )
//        }
//        data.value = posts
//    }
//
//    override fun shareById(id: Long) {
//        posts = posts.map {
//            if (it.id != id) it else it.copy(
//                shares = it.shares + 1
//            )
//        }
//        data.value = posts
//    }
//
//    override fun removeById(id: Long) {
//        posts = posts.filter { it.id != id }
//        data.value = posts
//    }
//}
