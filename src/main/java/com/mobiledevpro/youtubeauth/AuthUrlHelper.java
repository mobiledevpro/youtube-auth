package com.mobiledevpro.youtubeauth;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class help to create URL for oAuth
 * <p>
 * Created by Dmitriy V. Chernysh on 23.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 * <p>
 * #MobileDevPro
 */

class AuthUrlHelper {

    private static final String BASE_URL = "accounts.google.com";

    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_RESPONSE_TYPE = "response_type";
    private static final String PARAM_ACCESS_TYPE = "access_type";
    private static final String PARAM_SCOPE = "scope";
    private static final String PARAM_REDIRECT_URI = "redirect_uri";
    private static final String PARAM_PROMPT = "prompt";

    private static final String VALUE_RESPONSE_TYPE = "token";
    private static final String VALUE_ACCESS_TYPE = "online";
    private static final String VALUE_SCOPE = "https://www.googleapis.com/auth/youtube.upload " +
            "https://www.googleapis.com/auth/youtube.readonly " +
            "https://www.googleapis.com/auth/youtube";
    private static final String VALUE_REDIRECT_URI = "http://localhost";
    private static final String VALUE_PROMPT = "select_account";

    private static final String PARAM_RESULT_TOKEN = "access_token";
    private static final String PARAM_RESULT_TOKEN_TYPE = "token_type";

    private static AuthUrlHelper sAuthApiHelper;

    private AuthUrlHelper() {
    }

    static String createUrl(String appClientId) {
        if (sAuthApiHelper == null) {
            sAuthApiHelper = new AuthUrlHelper();
        }
        return sAuthApiHelper.getUrl(appClientId);
    }

    static String findToken(String url) {
        if (sAuthApiHelper == null) {
            sAuthApiHelper = new AuthUrlHelper();
        }
        return sAuthApiHelper.getToken(url);
    }

    /**
     * Create URL for get token
     *
     * @param clientId Client ID from Google Dev console
     * @return URL
     */
    private String getUrl(String clientId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath("o")
                .appendPath("oauth2")
                .appendPath("auth")
                .appendQueryParameter(PARAM_CLIENT_ID, clientId)
                .appendQueryParameter(PARAM_RESPONSE_TYPE, VALUE_RESPONSE_TYPE)
                .appendQueryParameter(PARAM_ACCESS_TYPE, VALUE_ACCESS_TYPE)
                .appendQueryParameter(PARAM_SCOPE, VALUE_SCOPE)
                .appendQueryParameter(PARAM_REDIRECT_URI, VALUE_REDIRECT_URI)
                .appendQueryParameter(PARAM_PROMPT, VALUE_PROMPT);

        return builder.build().toString();
    }

    /**
     * Get token from redirect url
     *
     * @param redirectUrl Redirect url
     * @return Token value
     */
    private String getToken(String redirectUrl) {
        Uri uri = Uri.parse(redirectUrl);

        String fragment = uri.getFragment();

        if (TextUtils.isEmpty(fragment)) return "";

        Map<String, String> map = new HashMap<>();
        String[] paramValueArray = fragment.split("&");
        String[] paramArray;

        for (String paramValue : paramValueArray) {
            paramArray = paramValue.split("=");
            map.put(paramArray[0], paramArray[1]);
        }

        //find token in the map
        String token = map.containsKey(PARAM_RESULT_TOKEN) ? map.get(PARAM_RESULT_TOKEN) : "";
        String tokenType = map.containsKey(PARAM_RESULT_TOKEN_TYPE) ? map.get(PARAM_RESULT_TOKEN_TYPE) + " " : "";

        return tokenType + token;
    }
}
