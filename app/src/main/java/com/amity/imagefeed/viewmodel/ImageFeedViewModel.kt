package com.amity.imagefeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.amity.socialcloud.sdk.social.AmitySocialClient
import com.amity.socialcloud.sdk.social.feed.AmityPost
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

@ExperimentalPagingApi
class ImageFeedViewModel : ViewModel() {

    fun getFeed(onFeedUpdated: (postPagingData: PagingData<AmityPost>) -> Unit): Completable {
        return AmitySocialClient.newFeedRepository()
            .getGlobalFeed()
            .build()
            .getPagingData()
            .doOnNext { onFeedUpdated.invoke(it) }
            .ignoreElements()
            .subscribeOn(Schedulers.io())
    }
}