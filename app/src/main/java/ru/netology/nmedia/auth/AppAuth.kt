package ru.netology.nmedia.auth

import ApiService
import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.datatransferobjects.PushToken
import ru.netology.nmedia.datatransferobjects.Token

class AppAuth private constructor(context: Context) {

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

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            val dto = PushToken(token ?: FirebaseMessaging.getInstance().token.await())

            try {
                ApiService.service.sendPushToken(dto)
            } catch (e: Exception) {

            }
        }
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
        @Volatile
        private var appAuth: AppAuth? = null

        fun getInstance(): AppAuth {
            return appAuth ?: throw IllegalStateException("Need to call init(context) before")
        }

        fun init(context: Context) {
            if (appAuth == null) {
                synchronized(this) {
                    if (appAuth == null) {
                        appAuth = AppAuth(context)
                    }
                }
            }
        }
    }
}