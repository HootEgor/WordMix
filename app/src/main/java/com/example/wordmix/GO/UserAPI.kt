package com.example.wordmix.GO

import com.example.wordmix.ui.theme.ScoreCell
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserAPI {
    @GET("user/{id}")
    suspend fun getUserInfo(@Path("id") userId: String): User

    @GET("score/leaders")
    suspend fun getLeaders(): ArrayList<ScoreCell>

    @POST("auth/login")
    suspend fun loginUser(@Body user: User): Response<String>
}
