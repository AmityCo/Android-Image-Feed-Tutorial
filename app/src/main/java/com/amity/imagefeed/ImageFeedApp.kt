package com.amity.imagefeed

import android.app.Application
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.AmityEndpoint

class ImageFeedApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AmityCoreClient
            .setup("enter your API Key", AmityEndpoint.SG)
            .subscribe()
    }
}
