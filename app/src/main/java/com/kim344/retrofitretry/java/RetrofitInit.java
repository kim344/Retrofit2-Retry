package com.kim344.retrofitretry.java;

import android.util.Log;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.kim344.retrofitretry.java.Constant.TIMEOUT_SECOND;
import static com.kim344.retrofitretry.java.Constant.URL_HOST;

public class RetrofitInit {

    private static RetrofitInit instance;

    RetrofitInit getInstance(){
        if (instance != null) {}
        else instance = new RetrofitInit();
        return instance;
    }

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL_HOST + "/")
            .client(unSafeOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private Service service = retrofit.create(Service.class);

    Service getService(){
        if (service != null) {}
        else service = retrofit.create(Service.class);
        return service;
    }

    private OkHttpClient unSafeOkHttpClient(){
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null,trustAllCerts,new SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECOND,TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECOND,TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor())
                    .sslSocketFactory(sslSocketFactory,(X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    }).build();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("Retrofit :", message + "");
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

}
