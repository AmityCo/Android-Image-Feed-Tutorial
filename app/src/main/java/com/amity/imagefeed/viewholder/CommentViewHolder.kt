package com.amity.imagefeed.viewholder

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amity.imagefeed.R
import com.amity.imagefeed.databinding.ListItemCommentBinding
import com.amity.imagefeed.readableFeedPostTime
import com.amity.socialcloud.sdk.core.file.AmityImage
import com.amity.socialcloud.sdk.social.comment.AmityComment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CommentViewHolder(private val binding: ListItemCommentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: AmityComment?) {
        presentHeader(comment)
        presentContent(comment)
        presentFooter(comment)
    }

    private fun presentHeader(comment: AmityComment?) {
        //render commenter's avatar
        Glide.with(itemView)
            .load(comment?.getUser()?.getAvatar()?.getUrl(AmityImage.Size.SMALL))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.avatarImageView)
        //render commenter's display name
        binding.displayNameTextView.text = comment?.getUser()?.getDisplayName() ?: "Unknown user"
        //render commented time
        binding.commentTimeTextView.text =
            comment?.getCreatedAt()?.millis?.readableFeedPostTime(itemView.context) ?: ""
    }


    private fun presentContent(comment: AmityComment?) {
        comment?.getData()
        //make sure that the comment contains text data
        if (comment?.getData() is AmityComment.Data.TEXT) {
            val commentText = (comment.getData() as AmityComment.Data.TEXT).getText()
            binding.descriptionTextview.text = commentText
        }
    }

    private fun presentFooter(comment: AmityComment?) {
        //render like count
        binding.likeCountTextview.text = getLikeCountString(comment?.getReactionCount() ?: 0)

        val isLikedByMe = comment?.getMyReactions()?.contains("like") == true
        val context = binding.root.context
        val highlightedColor = ContextCompat.getColor(context, R.color.teal_700)
        val inactiveColor = ContextCompat.getColor(context, R.color.dark_grey)
        if (isLikedByMe) {
            //present highlighted color if the comment is liked by me
            setLikeTextViewDrawableColor(highlightedColor)
        } else {
            //present inactive color if the comment isn't liked by me
            setLikeTextViewDrawableColor(inactiveColor)
        }
        //add or remove a like reaction when clicking like textview
        binding.likeCountTextview.setOnClickListener {
            if (isLikedByMe) {
                comment?.react()?.removeReaction("like")?.subscribe()
            } else {
                comment?.react()?.addReaction("like")?.subscribe()
            }
        }
    }

    private fun setLikeTextViewDrawableColor(@ColorInt color: Int) {
        for (drawable in binding.likeCountTextview.compoundDrawablesRelative) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
        binding.likeCountTextview.setTextColor(color)
    }

    private fun getLikeCountString(likeCount: Int): String {
        return itemView.context.resources.getQuantityString(
            R.plurals.amity_number_of_likes,
            likeCount,
            likeCount
        )
    }
}