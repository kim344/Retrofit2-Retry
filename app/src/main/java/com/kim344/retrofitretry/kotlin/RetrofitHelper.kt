package com.kim344.retrofitretry.kotlin

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitHelper {

    abstract class RetryAbleCallback<T> constructor(
        private val call: Call<T>,
        private val totalRetries: Int,
        private val context: Context
    ) :
        Callback<T> {
        private val TAG = RetryAbleCallback::class.java.simpleName
        private var retryCount = 0

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (!APIHelper.isCallSuccess(response))
                if (retryCount++ > totalRetries) {
                    Log.e(TAG, "Retrying onResponse API Call - ($retryCount / $totalRetries)")

                    AlertDialog.Builder(context).apply {
                        setMessage("서비스 연결이 원활하지않습니다.잠시 후 다시 시도해주시기 바랍니다.")
                        setCancelable(false)
                            .setPositiveButton(
                                "확인"
                            ) { dialog, id ->
                                retry()

                                dialog.dismiss()
                            }
                        create().apply {
                            setOnShowListener {
                                this.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setTextColor(Color.BLACK)
                            }
                            this.show()
                        }
                    }
                }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e("$TAG : onFailure", t.message)

            AlertDialog.Builder(context).apply {
                setMessage("서비스 연결이 원활하지않습니다.잠시 후 다시 시도해주시기 바랍니다.")
                setCancelable(false)
                    .setPositiveButton(
                        "확인"
                    ) { dialog, id ->
                        retry()

                        dialog.dismiss()
                    }
                create().apply {
                    setOnShowListener {
                        this.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    }
                    this.show()
                }
            }
        }

        fun test() {

        }

        fun onFinalResponse(call: Call<T>, response: Response<T>) {
            // Final Response Action
            Log.e("$TAG : onFinalResponse", "")
        }

        fun onFinalFailure(call: Call<T>, t: Throwable) {
            // Final Failure Action
            Log.e("$TAG : onFinalFailure", t.message)
        }

        private fun retry() {
            call.clone().enqueue(this)
        }
    }

    class APIHelper {

        var context: Context? = null

        fun <T> enqueueWithRetry(call: Call<T>, retryCount: Int, callback: Callback<T>) {
            call.enqueue(object : RetrofitHelper.RetryAbleCallback<T>(call, retryCount, context) {
                override fun onFinalResponse(call: Call<T>?, response: Response<T>?) {
                    callback.onResponse(call, response)
                }

                override fun onFinalFailure(call: Call<T>?, t: Throwable?) {
                    callback.onFailure(call, t)
                }
            })
        }

        fun <T> enqueueWithRetry(call: Call<T>, callback: Callback<T>) {
            enqueueWithRetry(call, DEFAULT_RETRIES, callback)
        }

        companion object {

            const val DEFAULT_RETRIES = 3

            fun isCallSuccess(response: Response<*>): Boolean {
                val code = response.code()
                return (code in 200..399)
            }
        }


    }
}