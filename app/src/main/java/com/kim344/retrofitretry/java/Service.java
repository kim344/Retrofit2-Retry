package com.kim344.retrofitretry.java;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Service {

    // Your End Point
    @GET("/answers?order=desc&sort=activity&site=stackoverflowt")
    Call<Model> getAnswers();

}
