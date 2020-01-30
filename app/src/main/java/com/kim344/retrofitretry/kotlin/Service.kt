package com.kim344.retrofitretry.kotlin

import retrofit2.Call
import retrofit2.http.GET

interface Service {

    // Your End Point
    @GET("/answers?order=desc&sort=activity&site=stackoverflow")
    fun getAnswers(): Call<Model>

}