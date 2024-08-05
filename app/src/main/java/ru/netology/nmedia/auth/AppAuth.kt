package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.*
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
    }


    @Synchronized
    fun setAuth(id: Long, token: String) {
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        _data.value = Token(id, token)
    }

    @Synchronized
    fun clearAuth() {
        prefs.edit { clear() }
        _data.value = null
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