package com.gregmcgowan.drownedinsound;

import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;

import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.loopj.android.http.PersistentCookieStore;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class DisBoardsApp extends Application {

    private final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "App";
    private PersistentCookieStore cookieStore;

    public static DisBoardsApp getApplication(Context context) {
	return (DisBoardsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
	super.onCreate();
	setupCookies();
    }

    private void setupCookies() {
	cookieStore = new PersistentCookieStore(this);
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

    public List<Cookie> getCookies() {
	return cookieStore.getCookies();
    }

    @Override
    public void onTerminate() {
	// TODO Auto-generated method stub
	super.onTerminate();
    }


}
