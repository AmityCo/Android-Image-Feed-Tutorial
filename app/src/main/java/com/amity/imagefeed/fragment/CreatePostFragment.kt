package com.amity.imagefeed.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amity.imagefeed.R
import com.amity.imagefeed.databinding.FragmentCreatePostBinding
import com.amity.imagefeed.viewmodel.CreatePostViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers

class CreatePostFragment : Fragment() {

    private val viewModel: CreatePostViewModel by viewModels()
    private var binding: FragmentCreatePostBinding? = null

    private var imageUri: Uri? = null

    private val openDocumentResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { imageUri ->
                    requireActivity().contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    renderPreviewImage(imageUri)
                    this.imageUri = imageUri
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.attachImageTextview?.setOnClickListener { openDocumentPicker() }
        binding?.createPostButton?.setOnClickListener { createPost() }
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        openDocumentResult.launch(intent)
    }

    private fun renderPreviewImage(uri: Uri) {
        binding?.createPostImageview?.let {
            it.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(it)
        }
    }

    private fun createPost() {
        showToast("Creating a post, please wait..")
        val postText = binding?.createPostEdittext?.text.toString()
        if (postText.isNotBlank() && imageUri != null) {
            viewModel.createImagePost(postText = postText,
                postImage = imageUri!!,
                onPostCreationError = {
                    showToast("Post creation error ${it.message}")
                },
                onPostCreationSuccess = {
                    findNavController().navigate(R.id.action_CreatePostFragment_to_ImageFeedFragment)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        } else {
            showToast("Either text or image is empty")
        }
    }

    private fun showToast(message: String) {
        Snackbar.make(binding!!.root, message, BaseTransientBottomBar.LENGTH_SHORT).show()
    }
}

