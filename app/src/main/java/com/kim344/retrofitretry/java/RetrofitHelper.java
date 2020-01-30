package com.kim344.retrofitretry.java;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitHelper {

    public abstract static class RetryAbleCallback<T> implements Callback<T> {

        private Context context;
        private int totalRetries;
        private final String TAG = RetryAbleCallback.class.getSimpleName();
        private final Call<T> call;
        private int retryCount = 0;

        RetryAbleCallback(Call<T> call, int totalRetries, Context context) {
            this.call = call;
            this.totalRetries = totalRetries;
            this.context = context;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (!APIHelper.isCallSuccess(response))
                if (retryCount++ > totalRetries){
                    Log.e(TAG, "Retrying onResponse API Call - (" + retryCount + " / " + totalRetries + ")");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("서비스 연결이 원활하지않습니다.잠시 후 다시 시도해주시기 바랍니다.");
                    builder.setCancelable(false)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            retry();

                                            dialog.dismiss();
                                        }
                                    });

                    final AlertDialog alert = builder.create();
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        }
                    });
                    alert.show();
                }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.e(TAG + " : onFailure", t.getMessage());

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("서비스 연결이 원활하지않습니다.잠시 후 다시 시도해주시기 바랍니다.");
            builder.setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    retry();

                                    dialog.dismiss();
                                }
                            });

            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                }
            });
            alert.show();
        }

        public void onFinalResponse(Call<T> call, Response<T> response) {
            // Final Response Action
            Log.e(TAG + " : onFinalResponse", "");
        }

        public void onFinalFailure(Call<T> call, Throwable t) {
            // Final Failure Action
            Log.e(TAG + " : onFinalFailure", t.getMessage());
        }

        private void retry() {
            call.clone().enqueue(this);
        }
    }

    public static class APIHelper {

        static final int DEFAULT_RETRIES = 3;
        Context context;

        public <T> void enqueueWithRetry(Call<T> call, final int retryCount, final Callback<T> callback) {
            call.enqueue(new RetryAbleCallback<T>(call,retryCount,context) {
                @Override
                public void onFinalResponse(Call<T> call, Response<T> response) {
                    callback.onResponse(call,response);
                }

                @Override
                public void onFinalFailure(Call<T> call, Throwable t) {
                    callback.onFailure(call,t
                    );
                }
            });
        }

        public <T> void enqueueWithRetry(Call<T> call, final Callback<T> callback) {
            enqueueWithRetry(call, DEFAULT_RETRIES, callback);
        }

        static boolean isCallSuccess(Response response) {
            int code = response.code();
            return (code >= 200 && code < 400);
        }


    }

}
