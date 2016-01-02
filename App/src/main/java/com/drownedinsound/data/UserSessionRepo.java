package com.drownedinsound.data;


/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface UserSessionRepo {

    String getAuthenticityToken();

    void setAuthenticityToken(String authenticityToken);

    boolean isUserLoggedIn();

    void clearSession();
}
