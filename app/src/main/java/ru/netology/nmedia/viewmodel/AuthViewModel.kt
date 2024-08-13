package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel(
    private val appAuth:AppAuth,
):ViewModel() {
    val authData= appAuth
        .data.asLiveData()

    val isAuthenticated: Boolean
        get()=authData.value?.token != null
}