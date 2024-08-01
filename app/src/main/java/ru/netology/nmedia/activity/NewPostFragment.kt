package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg

        private const val IMAGE_MAX_SIZE = 2048
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            if(it.resultCode ==ImagePicker.RESULT_ERROR){
                Toast.makeText(requireContext(), "image pick error", Toast.LENGTH_SHORT)
                    .show()
                return@registerForActivityResult
            }
            val uri = it.data?.data?:return@registerForActivityResult
            viewModel.setPhoto(uri,uri.toFile())
        }

        arguments?.textArg
            ?.let(binding.editContent::setText)

        requireActivity().addMenuProvider(
            object :MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_add_post,menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    if (menuItem.itemId == R.id.save) {
                        viewModel.changeContentAndSave(binding.content.text.toString())
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }else{
                        false
                    }

            },
            viewLifecycleOwner,
        )

        viewModel.photo.observe(viewLifecycleOwner){model ->
            if(model!=null){
                binding.preview.setImageURI(model.uri)
                binding.previewContainer.isVisible = true
            }else{
                binding.previewContainer.isGone = true
            }

        }
        binding.clear.setOnClickListener{
            viewModel.clearPhoto()
        }

        binding.takePhoto.setOnClickListener{
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(IMAGE_MAX_SIZE,IMAGE_MAX_SIZE)
                .createIntent {
                    launcher.launch(it)

                }
        }
        binding.pickPhoto.setOnClickListener{
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .maxResultSize(IMAGE_MAX_SIZE,IMAGE_MAX_SIZE)
                .createIntent {
                    launcher.launch(it)

                }
        }


//        binding.save.setOnClickListener {
//            viewModel.changeContentAndSave(binding.content.text.toString())
//            AndroidUtils.hideKeyboard(requireView())
//        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

//        binding.close.setOnClickListener {
//            viewModel.onCloseEditClicked()
//            findNavController().navigateUp()
//        }
        return binding.root
    }
}