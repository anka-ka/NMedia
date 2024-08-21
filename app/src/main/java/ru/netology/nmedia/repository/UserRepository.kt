package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostsApiService
import retrofit2.Response
import ru.netology.nmedia.datatransferobjects.Token
import javax.inject.Inject

class UserRepository @Inject constructor (private val apiService: PostsApiService) {

    suspend fun authenticate(login: String, password: String): Response<Token> {
        return apiService.authenticate(login, password)
    }
}