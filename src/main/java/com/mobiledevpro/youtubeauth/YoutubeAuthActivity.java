package com.mobiledevpro.youtubeauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Class for ...
 * <p>
 * Created by Dmitriy V. Chernysh on 22.06.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 * <p>
 * #MobileDevPro
 */

public class YoutubeAuthActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 9999;
    public static final String KEY_APP_CLIENT_ID = "key.app.client.id"; //value from google console -> OAuth 2.0 client IDs -> Client ID
    public static final String KEY_RESULT_TOKEN = "key.result.token";

    private String mAppClientId;
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private TextView mTvError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setup start activity animation
        overridePendingTransition(
                R.anim.anim_activity_enter_slide_up,
                android.R.anim.fade_out
        );

        setContentView(R.layout.activity_youtube_auth);

        initView();

        //get client id from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_APP_CLIENT_ID)) {
            mAppClientId = extras.getString(KEY_APP_CLIENT_ID);
        }

        //check if client id is not empty
        if (TextUtils.isEmpty(mAppClientId)) {
            showError(getResources().getString(R.string.error_client_id_is_empty));
            return;
        }

        //open url
        showWebView();
    }

    @Override
    public void finish() {
        super.finish();
        //setup finish activity animation
        overridePendingTransition(
                android.R.anim.fade_in,
                R.anim.anim_activity_exit_slide_down
        );
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mTvError = (TextView) findViewById(R.id.tv_error);
    }

    /**
     * Show web view
     */
    private void showWebView() {
        if (mWebView == null) return;
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUserAgentString(Browser.USER_AGENT_STRING);

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(
                new Browser(this, new Browser.Callbacks() {
                    @Override
                    public void onGetToken(String token) {
                        setSuccessResult(token);
                    }

                    @Override
                    public void onError(int errCode, String description) {
                        showError(description);
                    }
                })
        );
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    if (mWebView != null) {
                        mWebView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mWebView.loadUrl(AuthUrlHelper.createUrl(mAppClientId));
    }

    /**
     * Show errors
     *
     * @param errMessage Message
     */
    private void showError(String errMessage) {
        if (TextUtils.isEmpty(errMessage)) return;

        if (mTvError != null) {
            mTvError.setText(errMessage);
            mTvError.setVisibility(View.VISIBLE);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
        }
    }


    /**
     * Return result to calling activity
     *
     * @param token Token
     */
    private void setSuccessResult(String token) {
        Intent intent = new Intent();
        intent.putExtra(KEY_RESULT_TOKEN, token);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
