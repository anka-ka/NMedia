package ru.netology.nmedia.activity

import android.app.Activity
import android.app.Notification.EXTRA_TEXT
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.requestFocus()
        binding.save.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                setResult(RESULT_CANCELED)
            } else {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(EXTRA_TEXT, text)
                })
            }
            finish()
        }

        binding.close.setOnClickListener() {
            setResult(RESULT_CANCELED)
            finish()
        }

        val content = intent.getStringExtra(EXTRA_TEXT)
        if (content != null) {
            binding.content.setText(content)
            binding.editContent.visibility = View.VISIBLE
        }
    }
}

object NewPostContract :  ActivityResultContract<String?, String?>() {

    override fun createIntent(context: Context, input: String?) =
            Intent(context,  NewPostActivity::class.java).putExtra(EXTRA_TEXT, input)

    override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra(EXTRA_TEXT)

}