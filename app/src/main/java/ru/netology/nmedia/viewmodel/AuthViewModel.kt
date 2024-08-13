package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth:AppAuth,
):ViewModel() {
    val authData= appAuth
        .data.asLiveData()

    val isAuthenticated: Boolean
        get()=authData.value?.token != null
}