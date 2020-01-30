package com.kim344.retrofitretry.kotlin

import android.util.Log
import com.kim344.retrofitretry.java.RetrofitInit
import com.kim344.retrofitretry.java.Service
import com.kim344.retrofitretry.kotlin.Constant.TIMEOUT_SECOND
import com.kim344.retrofitretry.kotlin.Constant.URL_HOST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class RetrofitInit {
    private var instance: RetrofitInit? = null

    internal fun getInstance(): RetrofitInit {
        if (instance != null) {
        } else
            instance = RetrofitInit()
        return instance as RetrofitInit
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("$URL_HOST/")
        .client(unSafeOkHttpClient())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var service: Service? = retrofit.create(Service::class.java)

    internal fun getService(): Service? {
        if (service != null) {
        } else
            service = retrofit.create(Service::class.java)
        return service
    }

    private fun unSafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    x509Certificates: Array<X509Certificate>,
                    s: String
                ) {

                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    x509Certificates: Array<X509Certificate>,
                    s: String
                ) {

                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            return OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECOND.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECOND.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECOND.toLong(), TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor())
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { s, sslSession -> true }.build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            Log.e(
                "Retrofit :",
                message + ""
            )
        })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}