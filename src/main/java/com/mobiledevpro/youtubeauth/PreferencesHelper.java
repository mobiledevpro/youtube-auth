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

    private static final String KEY_ACCESS_CODE = "api.access.code";
    private static final String KEY_ACCESS_TOKEN = "api.access.token";
    private static final String KEY_ACCESS_TOKEN_EXPIRED_TIME = "api.access.token.expired.time";
    private static final String KEY_REFRESH_TOKEN = "api.refresh.code";

    private static PreferencesHelper sHelper;

    private SharedPreferences mMainPrefs;

    public static PreferencesHelper getInstance(Context context) {
        if (sHelper == null) {
            sHelper = new PreferencesHelper(context);
        }
        return sHelper;
    }

    private PreferencesHelper(Context context) {
        mMainPrefs = context.getSharedPreferences(KEY_PREFS_FILE_MAIN, Context.MODE_PRIVATE);
    }

    /**
     * Save access code
     */
    void setAccessCode(String accessCode) {
        SharedPreferences.Editor editor = mMainPrefs.edit();
        editor.putString(KEY_ACCESS_CODE, accessCode);
        editor.apply();
    }

    public String getAccessCode() {
        return mMainPrefs.getString(KEY_ACCESS_CODE, "");
    }
}
