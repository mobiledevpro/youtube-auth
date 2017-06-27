package com.mobiledevpro.youtubeauth;

import android.content.Context;
import android.text.TextUtils;

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

    /**
     * Check expiration of access token and refresh if it needed
     *
     * @param appContext     Context
     * @param oldAccessToken Old access token
     * @param callbacks      Callbacks
     */
    public void checkAndRefreshAccessTokenAsync(Context appContext,
                                                String oldAccessToken,
                                                final YoutubeTokenHelper.ICallbacks callbacks) {

        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(appContext);
        String refreshToken = preferencesHelper.getRefreshToken();

        if (TextUtils.isEmpty(refreshToken)) {
            if (callbacks != null) {
                callbacks.onFail(appContext.getResources().getString(R.string.error_refresh_token_empty));
            }
            return;
        }

        //check internet connection
        if (!RestClient.isDeviceOnline(appContext)) {
            if (callbacks != null) {
                callbacks.onFail(appContext.getString(R.string.message_check_internet_connection));
            }
            return;
        }

        long tokenExpirationTime = preferencesHelper.getAccessTokenExpirationTime();

        //check if token is not expired
        long currentTime = new Date().getTime();
        if (currentTime < tokenExpirationTime) {
            if (callbacks != null) {
                callbacks.onSuccess(oldAccessToken);
            }
            return;
        }

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

    /**
     * Get refresh token
     *
     * @param appContext Context
     * @return Token
     */
    public String getRefreshToken(Context appContext) {
        return PreferencesHelper.getInstance(appContext).getRefreshToken();
    }

    /**
     * Revoke token for log out
     *
     * @param appContext Context
     * @param callbacks  Callbacks
     */
    public void revokeToken(Context appContext, final YoutubeTokenHelper.ICallbacks callbacks) {
        final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(appContext);

        String refreshToken = preferencesHelper.getRefreshToken();

        if (TextUtils.isEmpty(refreshToken)) {
            if (callbacks != null) {
                callbacks.onFail(appContext.getString(R.string.error_token_empty));
            }
            return;
        }

        //check internet connection
        if (!RestClient.isDeviceOnline(appContext)) {
            if (callbacks != null) {
                callbacks.onFail(appContext.getString(R.string.message_check_internet_connection));
            }
            return;
        }

        final AccessToken.Revoke.Request request = new AccessToken.Revoke.Request();
        request.build(
                refreshToken
        );

        RestClient.getAccountGoogleInstance(appContext).revokeTokenAsync(
                request,
                new RestClient.ICallBacks() {
                    @Override
                    public void onSuccess(int respCode, Object respBody) {
                        preferencesHelper.clearToken();
                        callbacks.onSuccess("");
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


    /**
     * Exchange access code for access token and refresh token
     *
     * @param appContext Context
     * @param accessCode Access code
     * @param callbacks  Callbacks
     */
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

        RestClient.getGoogleApiInstance(appContext).exchangeCodeForTokenAsync(
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
                                preferencesHelper.cacheRefreshToken(refreshToken, tokenExpirationTime);

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

    /**
     * Refresh access token
     *
     * @param appContext   Context
     * @param refreshToken Refresh token
     * @param callbacks    Callbacks
     */
    private void refreshAccessTokenAsync(final Context appContext, final String refreshToken, final YoutubeTokenHelper.ICallbacks callbacks) {

        final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(appContext);

        //request a new access token
        final AccessToken.Refresh.Request request = new AccessToken.Refresh.Request();
        request.build(
                refreshToken,
                mClientId,
                mClientSecret
        );

        RestClient.getGoogleApiInstance(appContext).refreshAccessTokenAsync(
                request,
                new RestClient.ICallBacks() {
                    @Override
                    public void onSuccess(int respCode, Object respBody) {
                        if (respBody == null) {
                            if (callbacks != null) {
                                callbacks.onFail("Please, sign-in");
                            }
                            return;
                        }
                        AccessToken.Refresh.Response response = (AccessToken.Refresh.Response) respBody;
                        String accessToken = response.getAccessToken();
                        long tokenExpirationTime = new Date().getTime() + (response.getExpirationInSeconds() * 1000);

                        if (!TextUtils.isEmpty(accessToken)) {
                            if (callbacks != null) {
                                //cache refresh token for further requests
                                preferencesHelper.cacheRefreshToken(refreshToken, tokenExpirationTime);

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
