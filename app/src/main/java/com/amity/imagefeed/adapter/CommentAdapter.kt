package com.amity.imagefeed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.amity.imagefeed.databinding.ListItemCommentBinding
import com.amity.imagefeed.viewholder.CommentViewHolder
import com.amity.socialcloud.sdk.social.comment.AmityComment

class CommentAdapter :
    PagedListAdapter<AmityComment, CommentViewHolder>(CommentDiffCallback()) {

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ListItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}


private class CommentDiffCallback : DiffUtil.ItemCallback<AmityComment>() {

    override fun areItemsTheSame(oldItem: AmityComment, newItem: AmityComment): Boolean {
        return oldItem.getCommentId() == newItem.getCommentId()
    }

    override fun areContentsTheSame(oldItem: AmityComment, newItem: AmityComment): Boolean {
        return oldItem.getCommentId() == newItem.getCommentId()
                && oldItem.getUpdatedAt() == newItem.getUpdatedAt()
                && oldItem.getReactionCount() == newItem.getReactionCount()
    }
}