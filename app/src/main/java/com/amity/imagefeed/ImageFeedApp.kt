package com.amity.imagefeed

import android.app.Application
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.AmityEndpoint

class ImageFeedApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AmityCoreClient
            .setup("b3bee90c39d9a5644831d84e5a0d1688d100ddebef3c6e78", AmityEndpoint.SG)
            .subscribe()
    }
}