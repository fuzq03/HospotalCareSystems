package com.xyx.travelingshare.utils

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpGetRequest {
    fun sendOkHttpGetRequest(address: String, callback: Callback) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .get()
            .url(address)
            .build()
        client.newCall(request).enqueue(callback)
    }
}