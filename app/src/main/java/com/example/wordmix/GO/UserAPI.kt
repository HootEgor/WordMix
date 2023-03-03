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
    @GET("user/score/{id}")
    suspend fun getUserHistory(@Path("id") userId: String): ArrayList<ScoreCell>

    @GET("user/{id}")
    suspend fun getUserInfo(@Path("id") userId: String): User

    @POST("auth/register")
    suspend fun register(@Body user: User): Response<String>

    @POST("auth/login")
    suspend fun loginUser(@Body user: User): Response<String>

    @GET("score/leaders")
    suspend fun getLeaders(): ArrayList<ScoreCell>

    @POST("score")
    suspend fun saveScore(@Body score: ScoreCell): Response<ResponseBody>


}
