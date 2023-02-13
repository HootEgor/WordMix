package com.example.wordmix.ui.theme

import retrofit2.http.GET

interface UserApi {
    @GET("posts/1")
    suspend fun getPost(): User

    @GET("posts")
    suspend fun getPosts(): List<User>
}