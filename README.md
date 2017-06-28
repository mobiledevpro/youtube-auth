# youtube-auth
Android Module for Youtube Sign-In with Channel selection.

1. Clone or download module.
2. Import module into project:
   Android Studio -> File -> New -> Import module -> Select directory with source of downloaded module.
3. Add module to dependencies in the app level build.gradle:
   ```
   compile project(':youtube-auth')
   ```

4. Start Sign-In to Youtube:
```java
   YoutubeAuthManager.getInstance(CLIENT_ID, CLIENT_SECRET).startSignIn(
                this /*activity or fragment*/,
                R.style.AppTheme_NoActionBar /*Theme Resource ID (optional)*/,
                R.string.app_name_youtube_auth /*String Resource ID (optional)*/,
                R.drawable.ic_close_24dp /*Icon Resource ID (optional)*/
        );
```

5. Handle result:
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case YoutubeAuthActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    //this is a token for using in requests to Youtube Data API
                    if (data != null && data.getExtras().containsKey(YoutubeAuthManager.KEY_SIGN_IN_RESULT_TOKEN)) {
                        mToken = data.getStringExtra(YoutubeAuthManager.KEY_SIGN_IN_RESULT_TOKEN);
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errMessage = data != null ? data.getStringExtra(YoutubeAuthManager.KEY_SIGN_IN_RESULT_ERROR) : "Cancelled";
                    Toast.makeText(this, errMessage, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }
```

6. Sign-Out:

```java
YoutubeTokenHelper.getInstance(CLIENT_ID, CLIENT_SECRET).signOut(
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
