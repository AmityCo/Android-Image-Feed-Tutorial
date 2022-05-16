package com.amity.imagefeed.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.amity.imagefeed.R
import com.amity.imagefeed.adapter.ImagePostAdapter
import com.amity.imagefeed.databinding.FragmentImageFeedBinding
import com.amity.imagefeed.viewmodel.ImageFeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class ImageFeedFragment : Fragment() {

    private val viewModel: ImageFeedViewModel by viewModels()
    private lateinit var adapter: ImagePostAdapter

    private var binding: FragmentImageFeedBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageFeedBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getFeedData()
        binding?.fabCreatePost?.setOnClickListener { navigateToImageFeedPage() }
    }

    private fun setupRecyclerView() {
        adapter = ImagePostAdapter()
        binding?.feedRecyclerview?.layoutManager = LinearLayoutManager(context)
        binding?.feedRecyclerview?.adapter = adapter
        adapter.addLoadStateListener { loadStates ->
            when (loadStates.mediator?.refresh) {
                is LoadState.NotLoading -> {
                    handleEmptyState(adapter.itemCount)
                }
            }
        }
    }

    private fun getFeedData() {
        viewModel.getFeed {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun handleEmptyState(itemCount: Int) {
        binding?.progressBar?.visibility = View.GONE
        binding?.emptyFeedView?.visibility = if (itemCount > 0) View.GONE else View.VISIBLE
    }

    private fun navigateToImageFeedPage() {
        findNavController().navigate(R.id.action_ImageFeedFragment_to_CreatePostFragment)
    }
}