# youtube-auth
Android Module for Youtube Sign-In with Channel selection (like Youtube Android App)


```java
Intent intent = new Intent(this, YoutubeAuthActivity.class);
intent.putExtra(YoutubeAuthActivity.KEY_APP_CLIENT_ID, <OAUTH_CLIENT_ID_FROM_GOOGLE_DEV_CONSOLE>);
intent.putExtra(YoutubeAuthActivity.KEY_APP_THEME_RES_ID, R.style.AppTheme_NoActionBar); //optional
intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_TITLE_RES_ID, R.string.app_name_youtube_auth); //optional
intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_HOME_ICON_RES_ID, R.drawable.ic_close_24dp); //optional
startActivityForResult(intent, YoutubeAuthActivity.REQUEST_CODE);
```
