package com.amity.imagefeed.viewmodel

import androidx.lifecycle.ViewModel
import com.amity.socialcloud.sdk.AmityCoreClient
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class LoginViewModel : ViewModel() {

    fun login(
        userId: String,
        displayName: String,
        onLoginSuccess: () -> Unit,
        onLoginError: (throwable: Throwable) -> Unit
    ): Completable {
        return AmityCoreClient.login(userId)
            .displayName(displayName)
            .build()
            .submit()
            .subscribeOn(Schedulers.io())
            .doOnComplete { onLoginSuccess.invoke() }
            .doOnError { onLoginError.invoke(it) }
    }
}