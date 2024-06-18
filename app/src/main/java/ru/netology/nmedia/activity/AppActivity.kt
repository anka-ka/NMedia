package ru.netology.nmedia.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import android.Manifest

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationsPermission()
        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
                }
            }
            checkGoogleApiAvailability()
        }

        private fun requestNotificationsPermission() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                return
            }

            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return
            }

            requestPermissions(arrayOf(permission), 1)
        }

        private fun checkGoogleApiAvailability() {
            with(GoogleApiAvailability.getInstance()) {
                val code = isGooglePlayServicesAvailable(this@AppActivity)
                if (code == ConnectionResult.SUCCESS) {
                    return@with
                }
                if (isUserResolvableError(code)) {
                    getErrorDialog(this@AppActivity, code, 9000)?.show()
                    return
                }
                Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                    .show()
            }

            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                println(it)
            }
        }
    }