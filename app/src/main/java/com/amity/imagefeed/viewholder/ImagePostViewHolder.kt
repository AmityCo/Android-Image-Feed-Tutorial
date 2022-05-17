package com.amity.imagefeed.viewholder

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.amity.imagefeed.R
import com.amity.imagefeed.databinding.ListItemPostBinding
import com.amity.imagefeed.readableFeedPostTime
import com.amity.socialcloud.sdk.core.file.AmityImage
import com.amity.socialcloud.sdk.social.feed.AmityPost
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ImagePostViewHolder(private val binding: ListItemPostBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(post: AmityPost?) {
        presentHeader(post)
        presentContent(post)
        presentFooter(post)
    }

    private fun presentHeader(post: AmityPost?) {
        //render poster's avatar
        Glide.with(itemView)
            .load(post?.getPostedUser()?.getAvatar()?.getUrl(AmityImage.Size.SMALL))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.avatarImageView)
        //render poster's display name
        binding.displayNameTextView.text = post?.getPostedUser()?.getDisplayName() ?: "Unknown user"
        //render posted time
        binding.postTimeTextView.text =
            post?.getCreatedAt()?.millis?.readableFeedPostTime(itemView.context) ?: ""
    }


    private fun presentContent(post: AmityPost?) {
        //render image post
        //clear image cache from the view first
        binding.itemGalleryPostImageImageview.setImageDrawable(null)
        //make sure that the post contains children posts
        if (post?.getChildren()?.isNotEmpty() == true) {
            val childPost = post.getChildren()[0]
            //make sure that the child post is an image post
            if (childPost.getData() is AmityPost.Data.IMAGE
                && (childPost.getData() as AmityPost.Data.IMAGE).getImage() != null
            ) {
                val image = (childPost.getData() as AmityPost.Data.IMAGE).getImage()
                Glide.with(itemView)
                    .load(image?.getUrl(AmityImage.Size.LARGE))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.itemGalleryPostImageImageview)
            }
        }
        //render image post description
        val postDescription = (post?.getData() as? AmityPost.Data.TEXT)?.getText() ?: ""
        val displayName = post?.getPostedUser()?.getDisplayName() ?: "Unknown user"
        binding.descriptionTextview.text = "$displayName : $postDescription"
    }

    private fun presentFooter(post: AmityPost?) {
        //render like count
        binding.likeCountTextview.text = getLikeCountString(post?.getReactionCount() ?: 0)
        //render comment count
        binding.commentCountTextview.text = getCommentCountString(post?.getCommentCount() ?: 0)

        val isLikedByMe = post?.getMyReactions()?.contains("like") == true
        val context = binding.root.context
        val highlightedColor = ContextCompat.getColor(context, R.color.teal_700)
        val inactiveColor = ContextCompat.getColor(context, R.color.dark_grey)
        if (isLikedByMe) {
            //present highlighted color if the post is liked by me
            setLikeTextViewDrawableColor(highlightedColor)
        } else {
            //present inactive color if the post isn't liked by me
            setLikeTextViewDrawableColor(inactiveColor)
        }
        //add or remove a like reaction when clicking like textview
        binding.likeCountTextview.setOnClickListener {
            if (isLikedByMe) {
                post?.react()?.removeReaction("like")?.subscribe()
            } else {
                post?.react()?.addReaction("like")?.subscribe()
            }
        }
        //navigate to comment list screen when clicking comment textview
        binding.commentCountTextview.setOnClickListener {
            val bundle = bundleOf("postId" to post?.getPostId())
            Navigation.findNavController(binding.root).navigate(R.id.action_ImageFeedFragment_to_CommentListFragment, bundle)
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

    private fun getCommentCountString(reactionCount: Int): String {
        return itemView.context.resources.getQuantityString(
            R.plurals.amity_number_of_comments,
            reactionCount,
            reactionCount
        )
    }
}