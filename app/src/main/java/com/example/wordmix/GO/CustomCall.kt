package com.example.wordmix.GO

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomCall<T> : Call<T> {
    override fun enqueue(callback: Callback<T>) {
        // Implementation
        TODO("Not yet implemented")
    }

    override fun isExecuted(): Boolean {
        // Implementation
        TODO("Not yet implemented")
    }

    override fun clone(): Call<T> {
        // Implementation
        TODO("Not yet implemented")
    }

    override fun isCanceled(): Boolean {
        // Implementation
        TODO("Not yet implemented")
    }

    override fun request(): Request {
        TODO("Not yet implemented")
    }

    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        // Implementation
    }

    override fun execute(): Response<T> {
        // Implementation
        TODO("Not yet implemented")
    }

    // No-args constructor
    constructor()
}