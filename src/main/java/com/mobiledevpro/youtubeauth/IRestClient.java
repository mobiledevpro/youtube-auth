package com.mobiledevpro.youtubeauth;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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


    @FormUrlEncoded
    @POST("oauth2/v4/token")
    Call<AccessToken.Exchange.Response> getTokens(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("oauth2/v4/token")
    Call<AccessToken.Refresh.Response> refreshAccessToken(@FieldMap Map<String, String> map);

    @GET("o/oauth2/revoke")
    Call<ResponseBody> revokeToken(@QueryMap Map<String, String> map);

}
