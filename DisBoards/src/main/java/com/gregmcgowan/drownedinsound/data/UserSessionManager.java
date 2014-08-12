package com.gregmcgowan.drownedinsound.data;

import com.gregmcgowan.drownedinsound.data.network.CookieManager;

/**
 * Created by gregmcgowan on 12/08/2014.
 */
public class UserSessionManager {

    private CookieManager cookieManager;

    private String authenticityToken;

    public UserSessionManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;
    }

    public boolean isUserLoggedIn(){
        boolean loggedIn = false;
        if(cookieManager != null) {
            loggedIn = cookieManager.userIsLoggedIn();
        }
        return  loggedIn;
    }

    public void clearSession(){
        if(cookieManager != null) {
            cookieManager.clearCookies();
        }
        authenticityToken = null;
    }
}
