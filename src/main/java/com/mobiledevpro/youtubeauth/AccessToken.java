package com.mobiledevpro.youtubeauth;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for ...
 * <p>
 * Created by Dmitriy V. Chernysh on 27.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 * <p>
 * #MobileDevPro
 */

class AccessToken {

    /**
     * Exchange access code for access and refresh tokens
     */
    static class Exchange {
        static class Request {

            private static final String PARAM_ACCESS_CODE = "code";
            private static final String PARAM_CLIENT_ID = "client_id";
            private static final String PARAM_CLIENT_SECRET = "client_secret";
            private static final String PARAM_GRANT_TYPE = "grant_type";
            private static final String PARAM_REDIRECT_URL = "redirect_uri";

            private static final String VALUE_GRANT_TYPE = "authorization_code";
            private static final String VALUE_REDIRECT_URI = "http://localhost";

            private String accessCode;
            private String clientId;
            private String clientSecret;

            Map<String, String> getQueryParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PARAM_ACCESS_CODE, accessCode);
                params.put(PARAM_CLIENT_ID, clientId);
                params.put(PARAM_CLIENT_SECRET, clientSecret);
                params.put(PARAM_GRANT_TYPE, VALUE_GRANT_TYPE);
                params.put(PARAM_REDIRECT_URL, VALUE_REDIRECT_URI);
                return params;
            }

            void build(String accessCode, String clientId, String clientSecret) {
                this.accessCode = accessCode;
                this.clientId = clientId;
                this.clientSecret = clientSecret;
            }
        }

        static class Response {
            @SerializedName("access_token")
            private String accessToken;

            @SerializedName("token_type")
            private String tokenType;

            @SerializedName("expires_in")
            int expirationInSeconds;

            @SerializedName("refresh_token")
            String refreshToken;

            String getAccessToken() {
                return tokenType + " " + accessToken;
            }

            int getExpirationInSeconds() {
                return expirationInSeconds;
            }

            public String getRefreshToken() {
                return refreshToken;
            }
        }
    }

    /**
     * Refresh access token
     */
    static class Refresh {
        static class Request {

            private static final String PARAM_CLIENT_ID = "client_id";
            private static final String PARAM_CLIENT_SECRET = "client_secret";
            private static final String PARAM_GRANT_TYPE = "grant_type";
            private static final String PARAM_REFRESH_TOKEN = "refresh_token";

            private static final String VALUE_GRANT_TYPE = "refresh_token";

            private String refreshToken;
            private String clientId;
            private String clientSecret;

            Map<String, String> getQueryParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PARAM_REFRESH_TOKEN, refreshToken);
                params.put(PARAM_CLIENT_ID, clientId);
                params.put(PARAM_CLIENT_SECRET, clientSecret);
                params.put(PARAM_GRANT_TYPE, VALUE_GRANT_TYPE);
                return params;
            }

            void build(String refreshToken, String clientId, String clientSecret) {
                this.refreshToken = refreshToken;
                this.clientId = clientId;
                this.clientSecret = clientSecret;
            }
        }

        static class Response {
            @SerializedName("access_token")
            private String accessToken;

            @SerializedName("token_type")
            private String tokenType;

            @SerializedName("expires_in")
            int expirationInSeconds;

            String getAccessToken() {
                return tokenType + " " + accessToken;
            }

            int getExpirationInSeconds() {
                return expirationInSeconds;
            }
        }
    }

    /**
     * Revoke token
     */
    static class Revoke {
        static class Request {
            private static final String PARAM_TOKEN = "token";

            private String refreshToken;

            void build(String refreshToken) {
                this.refreshToken = refreshToken;
            }

            Map<String, String> getQueryParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PARAM_TOKEN, refreshToken);
                return params;
            }
        }
    }

}
