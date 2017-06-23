package com.mobiledevpro.youtubeauth;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Simple Web View client
 * <p>
 * Created by Dmitriy V. Chernysh on 23.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 * <p>
 * #MobileDevPro
 */

class Browser extends WebViewClient {

    interface Callbacks {
        void onGetToken(String token);

        void onError(int errCode, String description);
    }

    static final String USER_AGENT_STRING = "Mozilla/5.0 (Linux; Android " + BuildConfig.VERSION_CODE + "; Android Device) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.83 Mobile Safari/537.36";

    private Activity mActivity;
    private Callbacks mCallbacks;

    Browser(Activity activity, Callbacks callbacks) {
        mActivity = activity;
        mCallbacks = callbacks;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String token = AuthUrlHelper.findToken(url);
        //if token was found
        if (!TextUtils.isEmpty(token)) {
            if (mCallbacks != null) {
                mCallbacks.onGetToken(token);
            }
            return false;
        }

        String error = AuthUrlHelper.findErrors(url);
        if (!TextUtils.isEmpty(error)) {
            if (mCallbacks != null) {
                mCallbacks.onError(0, error);
            }
            return false;
        }

        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (mCallbacks != null) {
            mCallbacks.onError(errorCode, description);
        }
    }
}
