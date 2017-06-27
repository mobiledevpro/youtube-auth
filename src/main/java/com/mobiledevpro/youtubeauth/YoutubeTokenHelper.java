package com.mobiledevpro.youtubeauth;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

/**
 * Helper for getting and regreshing access token
 * <p>
 * Created by Dmitriy V. Chernysh on 26.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 * <p>
 * #MobileDevPro
 */

public class YoutubeTokenHelper {

    private String mClientId;
    private String mClientSecret;

    public interface ICallbacks {
        void onSuccess(String accessToken);

        void onFail(String errMessage);
    }

    private static YoutubeTokenHelper sHelper;

    private YoutubeTokenHelper(String clientId,
                               String clientSecret) {

        mClientId = clientId;
        mClientSecret = clientSecret;
    }

    public static YoutubeTokenHelper getInstance(String clientId,
                                                 String clientSecret) {
        if (sHelper == null) {
            sHelper = new YoutubeTokenHelper(clientId, clientSecret);
        }

        return sHelper;
    }

    public void checkAndRefreshAccessTokenAsync(Context appContext,
                                                String oldAccessToken,
                                                final YoutubeTokenHelper.ICallbacks callbacks) {

        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(appContext);
        String refreshToken = preferencesHelper.getRefreshToken(oldAccessToken);

        if (TextUtils.isEmpty(refreshToken)) {
            if (callbacks != null) {
                callbacks.onFail(appContext.getResources().getString(R.string.error_access_code_empty));
            }
            return;
        }

        long tokenExpirationTime = preferencesHelper.getAccessTokenExpirationTime(oldAccessToken);

        Log.d("youtube-auth", "YoutubeTokenHelper.checkAndRefreshAccessTokenAsync(): tokenExpirationTime - " + new Date(tokenExpirationTime).toString());
        Log.d("youtube-auth", "YoutubeTokenHelper.checkAndRefreshAccessTokenAsync(): current time        - " + new Date().toString());

        //check if token is not expired
        long currentTime = new Date().getTime() + 3600000;
        if (currentTime < tokenExpirationTime) {
            if (callbacks != null) {
                callbacks.onSuccess(oldAccessToken);
            }
            return;
        }

        //clear an old saved token
        preferencesHelper.removeOldAccessToken(oldAccessToken);

        //request a new access token
        refreshAccessTokenAsync(appContext, refreshToken, new ICallbacks() {
            @Override
            public void onSuccess(String accessToken) {
                if (callbacks != null) {
                    callbacks.onSuccess(accessToken);
                }
            }

            @Override
            public void onFail(String errMessage) {
                if (callbacks != null) {
                    callbacks.onFail(errMessage);
                }
            }
        });
    }


    void exchangeCodeForTokenAsync(final Context appContext,
                                   String accessCode,
                                   final YoutubeTokenHelper.ICallbacks callbacks) {

        final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(appContext);

        //request tokens
        final AccessToken.Exchange.Request request = new AccessToken.Exchange.Request();
        request.build(
                accessCode,
                mClientId,
                mClientSecret
        );

        RestClient.getInstance(appContext).exchangeCodeForTokenAsync(
                request,
                new RestClient.ICallBacks() {
                    @Override
                    public void onSuccess(int respCode, Object respBody) {
                        AccessToken.Exchange.Response response = (AccessToken.Exchange.Response) respBody;
                        String accessToken = response.getAccessToken();
                        String refreshToken = response.getRefreshToken();

                        long tokenExpirationTime = new Date().getTime() + (response.getExpirationInSeconds() * 1000);

                        if (!TextUtils.isEmpty(accessToken)) {
                            if (callbacks != null) {
                                //cache refresh token for further requests
                                preferencesHelper.cacheRefreshToken(refreshToken, accessToken, tokenExpirationTime);

                                callbacks.onSuccess(accessToken);
                            }
                        } else {
                            if (callbacks != null) {
                                callbacks.onFail("Access token is empty. Please, try again or contact to developer");
                            }
                        }
                    }

                    @Override
                    public void onFail(String errMessage) {
                        if (callbacks != null) {
                            callbacks.onFail(errMessage);
                        }
                    }
                }
        );
    }

    private void refreshAccessTokenAsync(final Context appContext, final String refreshToken, final YoutubeTokenHelper.ICallbacks callbacks) {

        final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(appContext);

        //request a new access token
        final AccessToken.Refresh.Request request = new AccessToken.Refresh.Request();
        request.build(
                refreshToken,
                mClientId,
                mClientSecret
        );

        RestClient.getInstance(appContext).refreshAccessTokenAsync(
                request,
                new RestClient.ICallBacks() {
                    @Override
                    public void onSuccess(int respCode, Object respBody) {
                        AccessToken.Refresh.Response response = (AccessToken.Refresh.Response) respBody;
                        String accessToken = response.getAccessToken();
                        long tokenExpirationTime = new Date().getTime() + (response.getExpirationInSeconds() * 1000);

                        if (!TextUtils.isEmpty(accessToken)) {
                            if (callbacks != null) {
                                //cache refresh token for further requests
                                preferencesHelper.cacheRefreshToken(refreshToken, accessToken, tokenExpirationTime);

                                callbacks.onSuccess(accessToken);
                            }
                        } else {
                            if (callbacks != null) {
                                callbacks.onFail("Access token is empty. Please, try again or contact to developer");
                            }
                        }
                    }

                    @Override
                    public void onFail(String errMessage) {
                        if (callbacks != null) {
                            callbacks.onFail(errMessage);
                        }
                    }
                }
        );
    }
}
