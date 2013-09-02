package com.gregmcgowan.drownedinsound;

import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class CookieManager {

    private final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "Cookie";
    private PersistentCookieStore cookieStore;

    public CookieManager(Context context) {
        setupCookies(context);
    }

    private void setupCookies(Context context) {
        cookieStore = new PersistentCookieStore(context);
        cookieStore.clearExpired(new Date());
        if (DisBoardsConstants.DEBUG) {
            debugCookies();
        }
        HttpClient.setCookies(cookieStore);
    }

    private void debugCookies() {
        List<Cookie> cookies = cookieStore.getCookies();
        Log.d(TAG, "And here are the cookies......");
        for (Cookie cookie : cookies) {
            Log.d(TAG,
                "Cookie = [" + cookie.getName() + ", " + cookie.getValue()
                    + "]");
        }

    }

    public boolean userIsLoggedIn() {
        String loggedInCookieValue = getCookieValue(DisBoardsConstants.LOGGED_IN_FIELD_NAME);
        String user_credentialsValue = getCookieValue(DisBoardsConstants.USER_CREDENTIALS_FIELD_NAME);
        String dis_session = getCookieValue(DisBoardsConstants.DIS_SESSION_FIELD_NAME);
        String hashed = getCookieValue(DisBoardsConstants.HASHED_FIELD_NAME);
        return "1".equals(loggedInCookieValue)
            && !TextUtils.isEmpty(user_credentialsValue)
            && !TextUtils.isEmpty(dis_session)
            && !TextUtils.isEmpty(hashed);
    }

    private String getCookieValue(String cookieToFind) {
        String value = null;
        List<Cookie> cookies = cookieStore.getCookies();
        if (!TextUtils.isEmpty(cookieToFind)) {
            for (Cookie cookie : cookies) {
                if (cookieToFind.equalsIgnoreCase(cookie.getName())) {
                    value = cookie.getValue();
                    break;
                }
            }
        }
        return value;
    }

    private List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

    public void clearCookies() {
        cookieStore.clear();
    }
}
