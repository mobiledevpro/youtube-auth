package com.mobiledevpro.youtubeauth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private static final String GOOGLE_API_URL = "https://www.googleapis.com";
    private static final String ACCOUNT_GOOGLE_URL = "https://accounts.google.com";

    private static final int HTTP_TIMEOUT = 25; //in seconds

    private static RestClient sGoogleApiClient;
    private static RestClient sAccountGoogleClient;
    private IRestClient mApiInterface;
    private Retrofit mRetrofit;

    interface ICallBacks {
        void onSuccess(int respCode, Object respBody);

        void onFail(String errMessage);
    }

    private RestClient(Context appContext, String url) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient
                .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS);

        //когда логирование включено не будет отображаться прогресс загрузки файла!!!
        //for loggining -->

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        //<!-- for loggining

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());

        builder.client(httpClient.build())
                .build();

        mRetrofit = builder.build();
        mApiInterface = mRetrofit.create(IRestClient.class);
    }

    static synchronized RestClient getGoogleApiInstance(Context appContext) {
        if (sGoogleApiClient == null) {
            sGoogleApiClient = new RestClient(appContext, GOOGLE_API_URL);
        }

        return sGoogleApiClient;
    }

    static synchronized RestClient getAccountGoogleInstance(Context appContext) {
        if (sAccountGoogleClient == null) {
            sAccountGoogleClient = new RestClient(appContext, ACCOUNT_GOOGLE_URL);
        }

        return sAccountGoogleClient;
    }

    void exchangeCodeForTokenAsync(AccessToken.Exchange.Request request, final ICallBacks callBacks) {
        Call<AccessToken.Exchange.Response> call = mApiInterface.getTokens(request.getQueryParams());

        Callback<AccessToken.Exchange.Response> callback = new Callback<AccessToken.Exchange.Response>() {
            @Override
            public void onResponse(Call<AccessToken.Exchange.Response> call, Response<AccessToken.Exchange.Response> response) {
                callBacks.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<AccessToken.Exchange.Response> call, Throwable t) {
                String errMsg = t.getMessage();
                if (TextUtils.isEmpty(errMsg)) {
                    errMsg = "Network error";
                }
                callBacks.onFail(errMsg);
            }
        };
        call.enqueue(callback);
    }

    /**
     * Refresh access token (Asynchronously)
     *
     * @param request   AccessToken.Refresh.Request
     * @param callBacks Callbacks
     */
    void refreshAccessTokenAsync(AccessToken.Refresh.Request request, final ICallBacks callBacks) {
        Call<AccessToken.Refresh.Response> call = mApiInterface.refreshAccessToken(request.getQueryParams());

        Callback<AccessToken.Refresh.Response> callback = new Callback<AccessToken.Refresh.Response>() {
            @Override
            public void onResponse(Call<AccessToken.Refresh.Response> call, Response<AccessToken.Refresh.Response> response) {
                callBacks.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<AccessToken.Refresh.Response> call, Throwable t) {
                String errMsg = t.getMessage();
                if (TextUtils.isEmpty(errMsg)) {
                    errMsg = "Network error";
                }
                callBacks.onFail(errMsg);
            }
        };
        call.enqueue(callback);
    }

    /**
     * Refresh access token (Synchronously)
     *
     * @param request   AccessToken.Refresh.Request
     * @param callBacks Callbacks
     */
    void refreshAccessTokenSync(AccessToken.Refresh.Request request, final ICallBacks callBacks) {
        Call<AccessToken.Refresh.Response> call = mApiInterface.refreshAccessToken(request.getQueryParams());

        try {
            Response<AccessToken.Refresh.Response> response = call.execute();
            callBacks.onSuccess(response.code(), response.body());
        } catch (IOException e) {
            callBacks.onFail("IOException: " + e.getLocalizedMessage());
        } catch (IllegalArgumentException ee) {
            callBacks.onFail("IllegalArgumentException: " + ee.getLocalizedMessage());
        }
    }

    /**
     * Revoke access to google account / revoke token (asynchronously)
     *
     * @param request   AccessToken.Revoke.Request
     * @param callBacks Callbacks
     */
    void revokeTokenAsync(AccessToken.Revoke.Request request, final ICallBacks callBacks) {
        Call<ResponseBody> call = mApiInterface.revokeToken(request.getQueryParams());

        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callBacks.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errMsg = t.getMessage();
                if (TextUtils.isEmpty(errMsg)) {
                    errMsg = "Network error";
                }
                callBacks.onFail(errMsg);
            }
        };
        call.enqueue(callback);
    }

    /**
     * Method for checking network connection
     *
     * @param context - application context
     * @return true - device online
     */
    static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
