package com.amity.imagefeed.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.core.file.AmityImage
import com.amity.socialcloud.sdk.core.file.AmityUploadResult
import com.amity.socialcloud.sdk.social.AmitySocialClient
import com.amity.socialcloud.sdk.social.feed.AmityPost
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CreatePostViewModel : ViewModel() {

    fun createImagePost(
        postText: String,
        postImage: Uri,
        onPostCreationSuccess: (AmityPost) -> Unit,
        onPostCreationError: (throwable: Throwable) -> Unit
    ): Completable {
        return uploadImage(postImage)
            .flatMap {
                AmitySocialClient.newPostRepository()
                    .createPost()
                    .targetMe()
                    .image(images = arrayOf(it))
                    .text(text = postText)
                    .build()
                    .post()
            }
            .subscribeOn(Schedulers.io())
            .doOnError { onPostCreationError.invoke(it) }
            .doOnSuccess { onPostCreationSuccess(it) }
            .ignoreElement()
    }

    private fun uploadImage(imageUri: Uri): Single<AmityImage> {
        return AmityCoreClient.newFileRepository()
            .uploadImage(uri = imageUri)
            .build()
            .transfer()
            .doOnNext {
                when (it) {
                    is AmityUploadResult.ERROR -> {
                        throw it.getError()
                    }
                    is AmityUploadResult.CANCELLED -> {
                        throw UPLOAD_CANCELLED_EXCEPTION
                    }
                }
            }
            .filter { it is AmityUploadResult.COMPLETE }
            .map { (it as AmityUploadResult.COMPLETE).getFile() }
            .firstOrError()
    }
}

val UPLOAD_CANCELLED_EXCEPTION = Exception("Upload has been cancelled")