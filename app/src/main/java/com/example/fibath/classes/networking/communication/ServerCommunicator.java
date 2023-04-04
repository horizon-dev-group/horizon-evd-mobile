package com.example.fibath.classes.networking.communication;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Looser on 6/7/2018.
 */

public class ServerCommunicator {
    //https://www.citylox.com/ethiorox/
    //    public static final String baseUrl = "http:/10.0.2.2/ethiorox_c/";
    //    public static final String baseUrl = "http:/192.168.137.189/ethiorox_c/"; // Maregne
    //    public static final String baseUrl = "http:/192.168.137.1/ethiorox_c/"; // Lemma
    private static Retrofit retrofit;

    public static Retrofit getConnection(String baseUrl) {
        OkHttpClient client = new OkHttpClient();

        try {
            client = new OkHttpClient.Builder().readTimeout(300, TimeUnit.SECONDS).connectTimeout(300,TimeUnit.SECONDS).sslSocketFactory(new TLSSocketFactory()).build();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
