package com.kim344.retrofitretry.java;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kim344.retrofitretry.R;

import retrofit2.Call;
import retrofit2.Response;

import static com.kim344.retrofitretry.java.Constant.RETRY_COUNT;

public class JavaMainActivity extends AppCompatActivity {

    RetrofitInit retrofit;
    Service service;
    Call<Model> call;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        retrofit = new RetrofitInit().getInstance();
        service = retrofit.getService();

        call = service.getAnswers();

        call.enqueue(new RetrofitHelper.RetryAbleCallback<Model>(call, RETRY_COUNT, context) {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                super.onResponse(call, response);
                // onResponse Success Action
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                super.onFailure(call, t);
                // onFailure Action
            }

            @Override
            public void onFinalResponse(Call<Model> call, Response<Model> response) {
                super.onFinalResponse(call, response);
                // onFinalResponse Action
            }

            @Override
            public void onFinalFailure(Call<Model> call, Throwable t) {
                super.onFinalFailure(call, t);
                // onFinalFailure Action
            }
        });
    }
}
