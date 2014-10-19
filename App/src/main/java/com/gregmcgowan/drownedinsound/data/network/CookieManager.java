package com.gregmcgowan.drownedinsound.data.network;

import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.squareup.okhttp.OkHttpClient;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;

public class CookieManager {

    private final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "Cookie";

    private java.net.CookieManager cookieManager;

    private CookieStore cookieStore;

    public CookieManager(OkHttpClient okHttpClient, SharedPreferences sharedPreferences) {
        setupCookies(okHttpClient, sharedPreferences);
    }

    private void setupCookies(OkHttpClient okHttpClient, SharedPreferences sharedPreferences) {
        cookieStore = new PersistentCookieStore(sharedPreferences);
        cookieManager = new java.net.CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        okHttpClient.setCookieHandler(cookieManager);
        if (DisBoardsConstants.DEBUG) {
            debugCookies();
        }
    }

    private void debugCookies() {
        List<HttpCookie> cookies = getCookies();
        Log.d(TAG, "And here are the cookies......");
        for (HttpCookie cookie : cookies) {
            Log.d(TAG,
                    "Cookie = [" + cookie.getName() + ", " + cookie.getValue()
                            + "]");
        }

    }

    private List<HttpCookie> getCookies() {
        return cookieManager.getCookieStore().getCookies();
    }

    public boolean userIsLoggedIn() {
        String loggedInCookieValue = getCookieValue(DisBoardsConstants.LOGGED_IN_FIELD_NAME);
        String user_credentialsValue = getCookieValue(
                DisBoardsConstants.USER_CREDENTIALS_FIELD_NAME);
        String dis_session = getCookieValue(DisBoardsConstants.DIS_SESSION_FIELD_NAME);
        return "1".equals(loggedInCookieValue)
                && !TextUtils.isEmpty(user_credentialsValue)
                && !TextUtils.isEmpty(dis_session);
    }

    private String getCookieValue(String cookieToFind) {
        List<HttpCookie> cookies = getCookies();
        String value = null;
        if (!TextUtils.isEmpty(cookieToFind)) {
            for (HttpCookie cookie : cookies) {
                if (cookieToFind.equalsIgnoreCase(cookie.getName())) {
                    value = cookie.getValue();
                    break;
                }
            }
        }
        return value;
    }

    public void clearCookies() {
        cookieStore.removeAll();
    }
}
