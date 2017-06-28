package com.mobiledevpro.youtubeauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
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

public class YoutubeAuthManager implements IYoutubeAuthManager {

    public static final int REQUEST_CODE_SIGN_IN = 9999;
    public static final String KEY_SIGN_IN_RESULT_TOKEN = "key.result.token";
    public static final String KEY_SIGN_IN_RESULT_ERROR = "key.result.error";

    private String mClientId;
    private String mClientSecret;

    private static YoutubeAuthManager sHelper;

    private YoutubeAuthManager(String clientId,
                               String clientSecret) {

        mClientId = clientId;
        mClientSecret = clientSecret;
    }

    public static YoutubeAuthManager getInstance(String clientId,
                                                 String clientSecret) {
        if (sHelper == null) {
            sHelper = new YoutubeAuthManager(clientId, clientSecret);
        }

        return sHelper;
    }


    @Override
    public void startSignIn(Fragment fragment,
                            @StyleRes int themeResId,
                            @StringRes int appbarTitleResId,
                            @DrawableRes int appbarHomeIconResId) {
        Intent intent = new Intent(fragment.getActivity(), YoutubeAuthActivity.class);
        intent.putExtra(YoutubeAuthActivity.KEY_APP_CLIENT_ID, mClientId);
        intent.putExtra(YoutubeAuthActivity.KEY_APP_CLIENT_SECRET, mClientSecret);
        if (themeResId > 0)
            intent.putExtra(YoutubeAuthActivity.KEY_APP_THEME_RES_ID, themeResId); //optional
        if (appbarTitleResId > 0)
            intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_TITLE_RES_ID, appbarTitleResId); //optional
        if (appbarHomeIconResId > 0)
            intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_HOME_ICON_RES_ID, appbarHomeIconResId); //optional
        fragment.startActivityForResult(intent, REQUEST_CODE_SIGN_IN);
    }


    @Override
    public void startSignIn(Activity activity,
                            @StyleRes int themeResId,
                            @StringRes int appbarTitleResId,
                            @DrawableRes int appbarHomeIconResId) {
        Intent intent = new Intent(activity, YoutubeAuthActivity.class);
        intent.putExtra(YoutubeAuthActivity.KEY_APP_CLIENT_ID, mClientId);
        intent.putExtra(YoutubeAuthActivity.KEY_APP_CLIENT_SECRET, mClientSecret);
        if (themeResId > 0)
            intent.putExtra(YoutubeAuthActivity.KEY_APP_THEME_RES_ID, themeResId); //optional
        if (appbarTitleResId > 0)
            intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_TITLE_RES_ID, appbarTitleResId); //optional
        if (appbarHomeIconResId > 0)
            intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_HOME_ICON_RES_ID, appbarHomeIconResId); //optional
        activity.startActivityForResult(intent, REQUEST_CODE_SIGN_IN);
    }


    @Override
    public void signOut(Context appContext, YoutubeAuthManager.ICallbacks callbacks) {
        revokeToken(appContext, callbacks);
    }


    @Override
    public void checkAndRefreshAccessTokenAsync(Context appContext,
                                                String oldAccessToken,
                                                YoutubeAuthManager.ICallbacks callbacks) {

        checkAndRefreshToken(appContext, oldAccessToken, true, callbacks);
    }

    @Override
    public void checkAndRefreshAccessTokenSync(Context appContext,
                                               String oldAccessToken,
                                               YoutubeAuthManager.ICallbacks callbacks) {

        checkAndRefreshToken(appContext, oldAccessToken, false, callbacks);
    }


    @Override
    public String getRefreshToken(Context appContext) {
        return PreferencesHelper.getInstance(appContext).getRefreshToken();
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
                                   final YoutubeAuthManager.ICallbacks callbacks) {

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
     * Refresh access token (asynchronously)
     *
     * @param appContext   Context
     * @param refreshToken Refresh token
     * @param callbacks    Callbacks
     */
    private void requestAccessToken(final Context appContext, boolean isAsyncRequest, final String refreshToken, final YoutubeAuthManager.ICallbacks callbacks) {

        final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(appContext);

        //request a new access token
        final AccessToken.Refresh.Request request = new AccessToken.Refresh.Request();
        request.build(
                refreshToken,
                mClientId,
                mClientSecret
        );

        //rest api callbacks
        RestClient.ICallBacks restClientCallBacks = new RestClient.ICallBacks() {
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
        };


        if (isAsyncRequest) {
            RestClient.getGoogleApiInstance(appContext).refreshAccessTokenAsync(
                    request,
                    restClientCallBacks
            );
        } else {
            RestClient.getGoogleApiInstance(appContext).refreshAccessTokenSync(
                    request,
                    restClientCallBacks
            );
        }
    }

    private void checkAndRefreshToken(Context appContext,
                                      String oldAccessToken,
                                      boolean isAsyncRequest,
                                      final YoutubeAuthManager.ICallbacks callbacks) {
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
        requestAccessToken(appContext, isAsyncRequest, refreshToken, new ICallbacks() {
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
     * Revoke token for log out
     *
     * @param appContext Context
     * @param callbacks  Callbacks
     */
    private void revokeToken(Context appContext, final YoutubeAuthManager.ICallbacks callbacks) {
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

        AccessToken.Revoke.Request request = new AccessToken.Revoke.Request();
        request.build(
                refreshToken
        );

        //rest client callbacks
        RestClient.ICallBacks restClientCallbacks = new RestClient.ICallBacks() {
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
        };


        RestClient.getAccountGoogleInstance(appContext).revokeTokenAsync(
                request,
                restClientCallbacks
        );

    }

}
