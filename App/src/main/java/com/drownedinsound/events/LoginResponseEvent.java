package com.drownedinsound.events;

public class LoginResponseEvent {

    private boolean success;

    private int loginUiId;

    public LoginResponseEvent(boolean success, int loginUiId) {
        this.success = success;
        this.loginUiId = loginUiId;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getLoginUiId() {
        return loginUiId;
    }
}
