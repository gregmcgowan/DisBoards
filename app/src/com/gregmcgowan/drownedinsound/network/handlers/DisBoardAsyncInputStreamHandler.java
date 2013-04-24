package com.gregmcgowan.drownedinsound.network.handlers;


import java.io.InputStream;

import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.loopj.android.http.InputStreamAsyncHttpResponseHandler;

public abstract class DisBoardAsyncInputStreamHandler extends InputStreamAsyncHttpResponseHandler {

    private boolean updateUI;
    
    public DisBoardAsyncInputStreamHandler(String identifier,boolean updateUI) {
	super.setIdentifier(identifier);
	setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(int statusCode,InputStream inputStream) {	
	doSuccessAction(statusCode, inputStream);
	HttpClient.requestHasCompleted(getIdentifier());
    }

    @Override
    public void handleFailure(Throwable e) {
	doFailureAction(e);
	HttpClient.requestHasCompleted(getIdentifier());
    }
    
    public boolean isUpdateUI() {
	return updateUI;
    }

    public void setUpdateUI(boolean updateUI) {
	this.updateUI = updateUI;
    }

    public abstract void doSuccessAction(int statusCode, InputStream inputStream);
    public abstract void doFailureAction(Throwable throwable);
    
}
