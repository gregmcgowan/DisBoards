package com.gregmcgowan.drownedinsound;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.cookie.Cookie;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class DisBoardsApp extends Application {

    private static final String MULTI_THREADED_EXECUTOR_SERVICE = "MULTI_THREADED_EXECUTOR_SERVICE";
    private static final float EXECUTOR_POOL_SIZE_PER_CORE = 1.5F;
    private final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "App";
    private PersistentCookieStore cookieStore;

    private ExecutorService multiThreadedExecutorService;

    public static DisBoardsApp getApplication(Context context) {
	return (DisBoardsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
	super.onCreate();
	initliaseDatabase();
	initliaseHttpClient();
    }

    private void initliaseDatabase() {
	// TODO add in clear call to clear out method. Which will
	// remove all old posts
	DatabaseHelper.getInstance(getApplicationContext()).initliase();

    }

    private void initliaseHttpClient() {
	HttpClient.setTimeout(DisBoardsConstants.NETWORK_REQUEST_TIMEOUT_MS);
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

    public ExecutorService getMultiThreadedExecutorService() {
	if (null == multiThreadedExecutorService
		|| multiThreadedExecutorService.isShutdown()) {
	    final int numThreads = Math.round(Runtime.getRuntime()
		    .availableProcessors() * EXECUTOR_POOL_SIZE_PER_CORE);
	    multiThreadedExecutorService = Executors.newFixedThreadPool(
		    numThreads, new DisBoardsThreadFactory(
			    MULTI_THREADED_EXECUTOR_SERVICE));

	    if (DisBoardsConstants.DEBUG) {
		Log.d(TAG, "MultiThreadExecutor created with " + numThreads
			+ " threads");
	    }
	}
	return multiThreadedExecutorService;
    }

    @Override
    public void onTerminate() {
	// TODO Auto-generated method stub
	super.onTerminate();
    }

}
