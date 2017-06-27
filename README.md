# youtube-auth
Android Module for Youtube Sign-In with Channel selection.

1. Clone or download module.
2. Import module into project:
   Android Studio -> File -> New -> Import module -> Select directory with source of downloaded module.
3. Add module to dependencies in the app level build.gradle:
   ```
   compile project(':youtube-auth')
   ```

4. Start activity to Sign-In:
```java
   Intent intent = new Intent(this, YoutubeAuthActivity.class);
   intent.putExtra(YoutubeAuthActivity.KEY_APP_CLIENT_ID, <OAUTH_CLIENT_ID_FROM_GOOGLE_DEV_CONSOLE>);
   intent.putExtra(YoutubeAuthActivity.KEY_APP_THEME_RES_ID, R.style.AppTheme_NoActionBar); //optional
   intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_TITLE_RES_ID, R.string.app_name_youtube_auth); //optional
   intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_HOME_ICON_RES_ID, R.drawable.ic_close_24dp); //optional
   startActivityForResult(intent, YoutubeAuthActivity.REQUEST_CODE);
```

5. Handle result:
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case YoutubeAuthActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    //this is a token for using in requests to Youtube Data API
                    if (data != null && data.getExtras().containsKey(YoutubeAuthActivity.KEY_RESULT_TOKEN)) {
                        mToken = data.getStringExtra(YoutubeAuthActivity.KEY_RESULT_TOKEN);
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errMessage = data != null ? data.getStringExtra(YoutubeAuthActivity.KEY_RESULT_ERROR) : "Cancelled";
                    Toast.makeText(this, errMessage, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }
```

6. Revoke access to account (Sign Out)

```java
YoutubeTokenHelper.getInstance(CLIENT_ID, CLIENT_SECRET).revokeToken(
                getApplicationContext(),
                new YoutubeTokenHelper.ICallbacks() {
                    @Override
                    public void onSuccess(String accessToken) {
                        //do something
                    }

                    @Override
                    public void onFail(String errMessage) {
                        Toast.makeText(getApplicationContext(), errMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
```

![youtube-auth](https://user-images.githubusercontent.com/5750211/27538571-507d444c-5a81-11e7-8102-503387e133af.gif)
