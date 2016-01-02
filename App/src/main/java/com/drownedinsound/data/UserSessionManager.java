package com.drownedinsound.data;

import com.drownedinsound.data.network.CookieManager;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by gregmcgowan on 12/08/2014.
 */
public class UserSessionManager implements UserSessionRepo{

    private CookieManager cookieManager;

    private String authenticityToken;

    @Inject
    public UserSessionManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        Timber.d("Setting auth token to " + authenticityToken);
        this.authenticityToken = authenticityToken;
    }

    public boolean isUserLoggedIn() {
        boolean loggedIn = false;
        if (cookieManager != null) {
            loggedIn = cookieManager.userIsLoggedIn();
        }
        return loggedIn;
    }

    public void clearSession() {
        if (cookieManager != null) {
            cookieManager.clearCookies();
        }
        authenticityToken = null;
    }
}
