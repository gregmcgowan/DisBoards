package com.gregmcgowan.drownedinsound.events;

public class LoginResponseEvent {

    private boolean success;
    
    public LoginResponseEvent(boolean success){
	this.setSuccess(success);
    }

    public boolean isSuccess() {
	return success;
    }

    public void setSuccess(boolean success) {
	this.success = success;
    }
    
}
