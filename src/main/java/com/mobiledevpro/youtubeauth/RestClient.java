package com.mobiledevpro.youtubeauth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RestAPI client with Retrofit
 * <p>
 * Created by Dmitriy V. Chernysh on 25.01.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 */

class RestClient {
    private static final String BASE_URL = "www.googleapis.com";

    private static final int HTTP_TIMEOUT = 25; //in seconds

    private static RestClient sRestClient;
    private IRestClient mApiInterface;
    private Retrofit mRetrofit;

    public interface ICallBacks {
        void onSuccess(int respCode, Object respBody);

        void onFail(String errMessage);
    }

    private RestClient(Context appContext) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient
                .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        builder.client(httpClient.build())
                .build();

        mRetrofit = builder.build();
        mApiInterface = mRetrofit.create(IRestClient.class);
    }

    public static synchronized RestClient getInstance(Context appContext) {
        if (sRestClient == null) {
            sRestClient = new RestClient(appContext);
        }

        return sRestClient;
    }


    /**
     * Method for checking network connection
     *
     * @param context - application context
     * @return true - device online
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
