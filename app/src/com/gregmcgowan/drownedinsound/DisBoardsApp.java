package com.gregmcgowan.drownedinsound;

import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class DisBoardsApp extends Application {

    private final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "App";
    private PersistentCookieStore cookieStore;

    public static DisBoardsApp getApplication(Context context) {
	return (DisBoardsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
	super.onCreate();
	initliaseDatabase();
	initliaseHttpClient();
	initliaseCrittercism();
    }

    private void initliaseDatabase() {
	//TODO add in clear call to clear out method. Which will 
	//remove all old posts
	DatabaseHelper.getInstance(getApplicationContext()).initliase();
	
    }

    private void initliaseHttpClient(){
	HttpClient.setTimeout(DisBoardsConstants.NETWORK_REQUEST_TIMEOUT_MS);
	setupCookies();
    }
    
    private void initliaseCrittercism() {
	Crittercism.init(getApplicationContext(), DisBoardsConstants.CRITTERCISM_APP_ID);
	// create the JSONObject. (Do not forget to import org.json.JSONObject!)
	JSONObject crittercismConfig = new JSONObject();
	try {
	    crittercismConfig.put("shouldCollectLogcat", true); // send logcat
								// data for
								// devices with
								// API Level 16
								// and higher
	} catch (JSONException je) {
	}

	Crittercism.init(getApplicationContext(),
		DisBoardsConstants.CRITTERCISM_APP_ID, crittercismConfig);

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
