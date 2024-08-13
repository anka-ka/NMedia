package ru.netology.nmedia.activity

import LoginViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import ru.netology.nmedia.databinding.LoginAndPasswordBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.repository.UserRepository
import ru.netology.nmedia.viewmodel.LoginViewModel


class LoginAndPasswordFragment : Fragment() {
    private val dependencyContainer = DependencyContainer.getInstance()

    private val viewModel: LoginViewModel by viewModels {
        val apiService = dependencyContainer.apiService
        val userRepository = UserRepository(apiService)

        LoginViewModelFactory(userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = LoginAndPasswordBinding.inflate(inflater, container, false)

        binding.signInButton.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            viewModel.login(login, password)
        }

        viewModel.isAuthenticated.observe(viewLifecycleOwner, Observer { isAuthenticated ->
            if (isAuthenticated) {
                val intent = Intent(requireContext(), AppActivity::class.java)
                startActivity(intent)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }
}