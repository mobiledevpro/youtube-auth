package com.mobiledevpro.youtubeauth;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Interface for Rest API client
 * <p>
 * Created by Dmitriy V. Chernysh on 26.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 */

interface IRestClient {


    /**
     * Get access and refresh token
     *
     * @param map
     * @return
     */
    @POST("oauth2/v4/token")
    Call<ResponseBody> getTokens(@QueryMap Map<String, String> map);

    @POST
    Call<ResponseBody> refreshAccessToken(@QueryMap Map<String, String> map);
}
