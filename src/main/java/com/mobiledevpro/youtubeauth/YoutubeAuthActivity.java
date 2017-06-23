package com.mobiledevpro.youtubeauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
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
    public static final String KEY_RESULT_ERROR = "key.result.error";

    public static final String KEY_APP_THEME_RES_ID = "key.app.theme.res.id";
    public static final String KEY_APPBAR_TITLE_RES_ID = "key.app.title.res.id";
    public static final String KEY_APPBAR_HOME_ICON_RES_ID = "key.app.home.icon.res.id";

    private
    @StyleRes
    int mThemeId;
    private
    @StringRes
    int mAppbarTitleResId;
    private
    @DrawableRes
    int mAppbarHomeIconResId;

    private String mAppClientId;
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private TextView mTvError;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //get client id from intent
            if (extras.containsKey(KEY_APP_CLIENT_ID)) {
                mAppClientId = extras.getString(KEY_APP_CLIENT_ID);
            }
            //get Theme resources id
            if (extras.containsKey(KEY_APP_THEME_RES_ID)) {
                mThemeId = extras.getInt(KEY_APP_THEME_RES_ID, 0);
                if (mThemeId > 0) {
                    setTheme(mThemeId);
                }
            }
            //get appbar title
            if (extras.containsKey(KEY_APPBAR_TITLE_RES_ID)) {
                mAppbarTitleResId = extras.getInt(KEY_APPBAR_TITLE_RES_ID, 0);
            }
            //get home as up indicator icon
            if (extras.containsKey(KEY_APPBAR_HOME_ICON_RES_ID)) {
                mAppbarHomeIconResId = extras.getInt(KEY_APPBAR_HOME_ICON_RES_ID, 0);
            }
        }

        //setup start activity animation
        overridePendingTransition(
                R.anim.anim_activity_enter_slide_up,
                android.R.anim.fade_out
        );

        setContentView(mThemeId > 0 ? R.layout.activity_youtube_auth : R.layout.activity_youtube_auth_fullscreen);
        initView();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initView() {
        //setup actionbar
        if (mThemeId > 0) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    if (mAppbarTitleResId > 0) actionBar.setTitle(mAppbarTitleResId);
                    if (mAppbarHomeIconResId > 0) {
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        actionBar.setHomeAsUpIndicator(mAppbarHomeIconResId);
                    }
                }
            }
        }

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
        setFailedResult(errMessage);
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

    private void setFailedResult(String errMessage) {
        Intent intent = new Intent();
        intent.putExtra(KEY_RESULT_ERROR, errMessage);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

}
