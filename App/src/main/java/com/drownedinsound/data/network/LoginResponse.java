package com.drownedinsound.data.network;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public class LoginResponse {

    private String authenticationToken;

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }
}
