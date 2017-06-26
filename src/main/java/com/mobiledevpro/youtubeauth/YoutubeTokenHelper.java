package com.mobiledevpro.youtubeauth;

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

    private static YoutubeTokenHelper sHelper;

    private YoutubeTokenHelper() {

    }

    public static YoutubeTokenHelper getInstance() {
        if (sHelper == null) {
            sHelper = new YoutubeTokenHelper();
        }

        return sHelper;
    }

    public String getAccessToken() {
        // TODO: 26.06.17 check if access_token is not expired

        // TODO: 26.06.17 check if has saved refresh_token

        // TODO: 26.06.17 refresh access_token

        return "";
    }
}
