package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel:ViewModel() {
    val authData= AppAuth.getInstance()
        .data.asLiveData()

    val isAuthenticated: Boolean
        get()=authData.value?.token != null
}