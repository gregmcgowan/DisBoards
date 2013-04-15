package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.File;

import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.loopj.android.http.FileAsyncBackgroundThreadHttpResponseHandler;

public abstract class DisBoardAsyncNetworkHandler extends
	FileAsyncBackgroundThreadHttpResponseHandler {

    private boolean updateUI;
    
    public DisBoardAsyncNetworkHandler(File file, String identifier,boolean updateUI) {
	super(file);
	super.setIdentifier(identifier);
	setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(int statusCode, File file) {
	HttpClient.requestHasCompleted(getIdentifier());
	doSuccessAction(statusCode, file);
    }

    @Override
    public void handleFailure(Throwable e, File response) {
	HttpClient.requestHasCompleted(getIdentifier());
	doFailureAction(e, response);
    }

    public abstract void doSuccessAction(int statusCode, File file);

    public abstract void doFailureAction(Throwable throwable, File response);

    public boolean isUpdateUI() {
	return updateUI;
    }

    public void setUpdateUI(boolean updateUI) {
	this.updateUI = updateUI;
    }

}
