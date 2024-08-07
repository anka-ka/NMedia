package ru.netology.nmedia.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import android.Manifest
import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationsPermission()
        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
                }
            }
            checkGoogleApiAvailability()


        viewModel.authData.observe(this) {
            invalidateOptionsMenu()
        }


        val viewModel by viewModels<AuthViewModel>()
            addMenuProvider(
                object : MenuProvider{
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.auth_menu, menu)
                        viewModel.authData.observe(this@AppActivity){
                            val isAuthenticated = viewModel.isAuthenticated

                            menu.setGroupVisible(R.id.authenticated, !isAuthenticated)
                            menu.setGroupVisible(R.id.unauthenticated, isAuthenticated)

                        }

                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                        when (menuItem.itemId) {
                            R.id.sing_in -> {
                                findNavController(R.id.nav_host_fragment)
                                    .navigate(R.id.action_appActivity_to_loginAndPasswordFragment)
                                AppAuth.getInstance().setAuth(5, "x-token")
                                true
                            }

                            R.id.sing_up -> {
                                true
                            }

                            R.id.logout -> {
                                AppAuth.getInstance().clearAuth()
                                true
                            }

                            else -> false
                        }
                }
            )
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