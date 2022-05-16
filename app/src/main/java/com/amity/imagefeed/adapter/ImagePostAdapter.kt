package com.amity.imagefeed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.amity.imagefeed.databinding.ListItemPostBinding
import com.amity.imagefeed.viewholder.ImagePostViewHolder
import com.amity.socialcloud.sdk.social.feed.AmityPost

class ImagePostAdapter :
    PagingDataAdapter<AmityPost, ImagePostViewHolder>(ImagePostDiffCallback()) {

    override fun onBindViewHolder(holder: ImagePostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePostViewHolder {
        return ImagePostViewHolder(
            ListItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}


private class ImagePostDiffCallback : DiffUtil.ItemCallback<AmityPost>() {

    override fun areItemsTheSame(oldItem: AmityPost, newItem: AmityPost): Boolean {
        return oldItem.getPostId() == newItem.getPostId()
    }

    override fun areContentsTheSame(oldItem: AmityPost, newItem: AmityPost): Boolean {
        return oldItem.getPostId() == newItem.getPostId()
                && oldItem.getUpdatedAt() == newItem.getUpdatedAt()
                && oldItem.getReactionCount() == newItem.getReactionCount()
    }
}