package com.example.makeev.myfirstapplication.utils

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class UserSelectionInterceptor : Interceptor {

    var email: String = ""
    var password: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = Credentials.basic(email, password)
        val request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
                .header("Authorization", credentials)
                .build()
        return chain.proceed(authenticatedRequest)
    }
}
