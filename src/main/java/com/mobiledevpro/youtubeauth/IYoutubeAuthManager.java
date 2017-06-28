package com.mobiledevpro.youtubeauth;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

/**
 * Interface for Youtube Auth manager
 * <p>
 * Created by Dmitriy V. Chernysh on 28.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 * <p>
 * #MobileDevPro
 */

public interface IYoutubeAuthManager {

    interface ICallbacks {
        void onSuccess(String accessToken);

        void onFail(String errMessage);
    }

    /**
     * Start Sign-In to Youtube from fragment
     *
     * @param fragment            Fragment
     * @param themeResId          Theme Resource ID (optional)
     * @param appbarTitleResId    String ID (optional)
     * @param appbarHomeIconResId Icon ID (optional)
     */
    void startSignIn(Fragment fragment,
                     @StyleRes int themeResId,
                     @StringRes int appbarTitleResId,
                     @DrawableRes int appbarHomeIconResId);

    /**
     * Start Sign-In to Youtube from activity
     *
     * @param activity            Activity
     * @param themeResId          Theme Resource ID (optional)
     * @param appbarTitleResId    String ID (optional)
     * @param appbarHomeIconResId Icon ID (optional)
     */
    void startSignIn(Activity activity,
                     @StyleRes int themeResId,
                     @StringRes int appbarTitleResId,
                     @DrawableRes int appbarHomeIconResId);

    /**
     * Youtube Sign-Out
     *
     * @param appContext Context
     * @param callbacks  Callbacks
     */
    void signOut(Context appContext, IYoutubeAuthManager.ICallbacks callbacks);

    /**
     * Check expiration of access token and refresh if it needed (Asynchronously)
     *
     * @param appContext     Context
     * @param oldAccessToken Old access token
     * @param callbacks      Callbacks
     */
    void checkAndRefreshAccessTokenAsync(Context appContext,
                                         String oldAccessToken,
                                         YoutubeAuthManager.ICallbacks callbacks);

    /**
     * Check expiration of access token and refresh if it needed (Synchronously)
     *
     * @param appContext     Context
     * @param oldAccessToken Old access token
     * @param callbacks      Callbacks
     */
    void checkAndRefreshAccessTokenSync(Context appContext,
                                        String oldAccessToken,
                                        YoutubeAuthManager.ICallbacks callbacks);

    /**
     * Get refreshed token
     *
     * @param appContext Context
     * @return Token
     */
    String getRefreshToken(Context appContext);
}
