package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.databinding.FragmentImageFullScreenBinding
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R

class ImageFullScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImageFullScreenBinding.inflate(inflater, container, false)

        val imageUrl = arguments?.getString("imageUrl")


        binding.cancel.setOnClickListener {
            findNavController().navigate(R.id.action_imageFullScreenFragment_to_feedFragment)
        }

        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.fullScreenImageView)
        }

        return binding.root
    }
}