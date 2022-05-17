package com.amity.imagefeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.amity.socialcloud.sdk.social.AmitySocialClient
import com.amity.socialcloud.sdk.social.comment.AmityComment
import com.amity.socialcloud.sdk.social.comment.AmityCommentSortOption
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class CommentListViewModel : ViewModel() {

    fun getComments(
        postId: String,
        onCommentListUpdated: (commentPagedList: PagedList<AmityComment>) -> Unit
    ): Completable {
        return AmitySocialClient.newCommentRepository()
            .getComments()
            .post(postId = postId)
            .includeDeleted(false)
            .sortBy(AmityCommentSortOption.LAST_CREATED)
            .build()
            .query()
            .doOnNext { onCommentListUpdated.invoke(it) }
            .ignoreElements()
            .subscribeOn(Schedulers.io())
    }

    fun createComment(
        postId: String,
        commentText: String,
        onCommentCreationSuccess: (AmityComment) -> Unit,
        onCommentCreationError: (throwable: Throwable) -> Unit
    ): Completable {
        return AmitySocialClient.newCommentRepository()
            .createComment()
            .post(postId = postId)
            .with()
            .text(text = commentText)
            .build()
            .send()
            .doOnSuccess { onCommentCreationSuccess.invoke(it) }
            .doOnError { onCommentCreationError.invoke(it) }
            .ignoreElement()
            .subscribeOn(Schedulers.io())
    }
}