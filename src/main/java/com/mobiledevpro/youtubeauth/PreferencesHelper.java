package com.mobiledevpro.youtubeauth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared Preferences Helper
 * <p>
 * Created by Dmitriy V. Chernysh on 26.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 * <p>
 * #MobileDevPro
 */

class PreferencesHelper {

    private static final String KEY_PREFS_FILE_MAIN = "youtube.auth.preferences";

    private static final String KEY_REFRESH_TOKEN = "api.refresh.token";
    private static final String KEY_ACCESS_TOKEN_EXPIRED_TIME = "api.access.token.expired.time";

    private static PreferencesHelper sHelper;

    private SharedPreferences mMainPrefs;

    static PreferencesHelper getInstance(Context context) {
        if (sHelper == null) {
            sHelper = new PreferencesHelper(context);
        }
        return sHelper;
    }

    private PreferencesHelper(Context context) {
        mMainPrefs = context.getSharedPreferences(KEY_PREFS_FILE_MAIN, Context.MODE_PRIVATE);
    }

    /**
     * Save refresh token
     */
    void cacheRefreshToken(String refreshToken, String accessToken, long expirationTimeInMs) {
        SharedPreferences.Editor editor = mMainPrefs.edit();
        editor.putString(KEY_REFRESH_TOKEN + "___" + accessToken.replace(" ", ""), refreshToken);
        editor.putLong(KEY_ACCESS_TOKEN_EXPIRED_TIME + "___" + accessToken.replace(" ", ""), expirationTimeInMs);
        editor.apply();
    }

    /**
     * Get refresh token
     *
     * @param accessToken Old access token
     * @return Access code
     */
    String getRefreshToken(String accessToken) {
        return mMainPrefs.getString(KEY_REFRESH_TOKEN + "___" + accessToken.replaceAll(" ", ""), "");
    }


    /**
     * Get saved expiration time for access token
     *
     * @param accessToken Token
     * @return Time in ms
     */
    long getAccessTokenExpirationTime(String accessToken) {
        return mMainPrefs.getLong(KEY_ACCESS_TOKEN_EXPIRED_TIME + "___" + accessToken.replaceAll(" ", ""), 0);
    }

    /**
     * Clear old token
     *
     * @param oldAccessToken Old Token
     */
    void removeOldAccessToken(String oldAccessToken) {
        SharedPreferences.Editor editor = mMainPrefs.edit();
        editor.remove(KEY_REFRESH_TOKEN + "___" + oldAccessToken.replaceAll(" ", ""));
        editor.remove(KEY_ACCESS_TOKEN_EXPIRED_TIME + "___" + oldAccessToken.replaceAll(" ", ""));
        editor.apply();
    }
}
