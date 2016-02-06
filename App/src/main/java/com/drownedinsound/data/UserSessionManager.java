package com.drownedinsound.data;

import com.drownedinsound.data.network.CookieManager;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by gregmcgowan on 12/08/2014.
 */
public class UserSessionManager implements UserSessionRepo {

    public static final String AUTH_TOKEN = "AUTH_TOKEN";

    private static final String USER_SELECTED_LURK = "USER_SELECTED_LURK";

    private CookieManager cookieManager;

    private AppPreferences appPreferences;

    @Inject
    public UserSessionManager(AppPreferences appPreferences,
            CookieManager cookieManager) {
        this.appPreferences = appPreferences;
        this.cookieManager = cookieManager;
    }

    @Override
    public String getAuthenticityToken() {
        return appPreferences.getStringSharedPreference(AUTH_TOKEN);
    }

    @Override
    public void setAuthenticityToken(String authenticityToken) {
        Timber.d("Setting auth token to " + authenticityToken);
        appPreferences.setStringSharedPreference(AUTH_TOKEN, authenticityToken);
    }

    @Override
    public boolean userSelectedLurk(){
        return appPreferences.getBooleanSharedPreference(USER_SELECTED_LURK);
    }

    @Override
    public void setUserSelectedLurk(boolean lurk) {
        appPreferences.setBooleanSharedPreference(USER_SELECTED_LURK, lurk);
    }

    @Override
    public boolean isUserLoggedIn() {
        boolean loggedIn = false;
        if (cookieManager != null) {
            loggedIn = cookieManager.userIsLoggedIn();
        }
        return loggedIn;
    }

    @Override
    public void clearSession() {
        if (cookieManager != null) {
            cookieManager.clearCookies();
        }
        appPreferences.clearSharedPreferences();
    }
}
