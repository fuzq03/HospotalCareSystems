package com.xyx.travelingshare.utils

import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class HttpPostRequest {
    private val JSON = MediaType.parse("application/json; charset=utf-8")

    fun okhttpPost(url: String, requestBody: RequestBody, callback: Callback) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(callback)
    }
}