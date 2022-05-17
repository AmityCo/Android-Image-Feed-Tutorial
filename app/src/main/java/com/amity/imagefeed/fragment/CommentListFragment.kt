package com.amity.imagefeed.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amity.imagefeed.adapter.CommentAdapter
import com.amity.imagefeed.databinding.FragmentCommentListBinding
import com.amity.imagefeed.viewmodel.CommentListViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch

class CommentListFragment : Fragment() {

    private lateinit var adapter: CommentAdapter
    private val viewModel: CommentListViewModel by viewModels()
    private var binding: FragmentCommentListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getComments()
        binding?.commentCreateTextView?.setOnClickListener { createComment() }
    }

    private fun setupRecyclerView() {
        adapter = CommentAdapter()
        binding?.commentRecyclerview?.layoutManager = LinearLayoutManager(context)
        binding?.commentRecyclerview?.adapter = adapter
    }

    private fun getComments() {
        val postId = arguments?.getString("postId")
        postId?.let {
            viewModel.getComments(postId = postId) {
                lifecycleScope.launch {
                    handleEmptyState(it.size)
                    adapter.submitList(it)
                }
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    private fun createComment() {
        showToast("Creating comment.. please wait")
        val postId = arguments?.getString("postId")
        val commentText = binding?.commentEditText?.text ?: ""
        if (commentText.isNotBlank() && postId != null) {
            viewModel.createComment(postId = postId, commentText = commentText.toString(),
                onCommentCreationSuccess = {
                    binding?.commentEditText?.setText("")
                    showToast("Comment was created successfully")
                },
                onCommentCreationError = {
                    binding?.commentEditText?.setText("")
                    showToast("Comment error : ${it.message}")
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        } else {
            showToast("Comment error : postId or comment is empty")
        }
    }

    private fun handleEmptyState(itemCount: Int) {
        binding?.progressBar?.visibility = View.GONE
        binding?.emptyCommentView?.visibility = if (itemCount > 0) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Snackbar.make(binding!!.root, message, LENGTH_SHORT).show()
    }
}