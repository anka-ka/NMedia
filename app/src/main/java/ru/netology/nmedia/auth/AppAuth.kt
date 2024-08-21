package ru.netology.nmedia.auth

import ru.netology.nmedia.api.PostsApiService
import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.datatransferobjects.PushToken
import ru.netology.nmedia.datatransferobjects.Token

import javax.inject.Inject
import javax.inject.Singleton

@Singleton

class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val firebaseMessaging: FirebaseMessaging,) {

    private  val ID_KEY = "ID_KEY"
    private  val TOKEN_KEY = "TOKEN_KEY"

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _data = MutableStateFlow<Token?>(null)
    val data: StateFlow<Token?> = _data.asStateFlow()

    init {
        val id = prefs.getLong(ID_KEY, 0)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id != 0L && token != null) {
            _data.value = Token(id, token)
        } else {
            prefs.edit { clear() }
        }
        sendPushToken()
    }


    @Synchronized
    fun setAuth(id: Long, token: String) {
        prefs.edit()
            .putLong(ID_KEY, id)
            .putString(TOKEN_KEY, token)
            .apply()
        _data.value = Token(id, token)
        sendPushToken()
    }

    @Synchronized
    fun clearAuth() {
        prefs.edit()
            .clear()
            .apply()
        _data.value = null
        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint{
        fun getApiService(): PostsApiService

    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            val dto = PushToken(token ?: firebaseMessaging.token.await())
            try {
                val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
                entryPoint.getApiService().sendPushToken(dto)
            } catch (e: Exception) {

            }
        }
    }
}