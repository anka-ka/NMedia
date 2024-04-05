package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.RepositoryInMemory



class PostViewModel : ViewModel() {

    private val repository: PostRepository = RepositoryInMemory()

    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
   // fun share() = repository.share()

    fun cutLongNumbers(number: Int): String {
        val formattedNumber = when {
            number < 1000 -> number.toString()
            number < 10_000 -> "${number / 1000}.${(number % 1000) / 100}K"
            number in 10_000..1_000_000 -> (number / 1000).toString() + "K"
            else -> "${number / 1_000_000}.${number % 1_000_000 / 100_000}M"
        }
        return formattedNumber
    }
}
