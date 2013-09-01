package com.gregmcgowan.drownedinsound.network.handlers;


import java.io.InputStream;

import org.apache.http.Header;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UserNotLoggedInException;
import com.loopj.android.http.InputStreamAsyncHttpResponseHandler;

public abstract class DisBoardAsyncInputStreamHandler extends InputStreamAsyncHttpResponseHandler {

    
    
    private boolean updateUI;
    
    public DisBoardAsyncInputStreamHandler(String identifier,boolean updateUI) {
	super.setIdentifier(identifier);
	setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(int statusCode,Header[] headers, InputStream inputStream) {	
	Log.d(DisBoardsConstants.LOG_TAG_PREFIX + this.getClass(), "Request was successful");
	doSuccessAction(statusCode, headers, inputStream);
	HttpClient.requestHasCompleted(getIdentifier());
    }

    @Override
    public void handleFailure(Throwable e) {
	Throwable cause = e.getCause();
	Throwable causeOfCause = cause.getCause();
	if(causeOfCause.getClass().equals(UserNotLoggedInException.class)) {
	    Log.d("NOTLOGGEDIN", "detected that the user is not logged in");
	} else {
	    doFailureAction(e);   
	}
	Log.d(DisBoardsConstants.LOG_TAG_PREFIX + this.getClass(), "Request failed throwable "+ e.getClass() + " cause "+ causeOfCause.getClass());
	
	HttpClient.requestHasCompleted(getIdentifier());
    }
    
    public boolean isUpdateUI() {
	return updateUI;
    }

    public void setUpdateUI(boolean updateUI) {
	this.updateUI = updateUI;
    }

    public abstract void doSuccessAction(int statusCode, Header[] headers, InputStream inputStream);
    public abstract void doFailureAction(Throwable throwable);
    
}
