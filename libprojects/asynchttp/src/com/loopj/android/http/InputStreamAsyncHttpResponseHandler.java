package com.loopj.android.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import android.os.Message;

public abstract class InputStreamAsyncHttpResponseHandler extends
	AsyncHttpResponseHandler {
    
    @Override
    void sendResponseMessage(HttpResponse response) {
	StatusLine status = response.getStatusLine();
	if (status.getStatusCode() >= 300) {
	    handleFailure(new HttpResponseException(
		    status.getStatusCode(), status.getReasonPhrase()));
	} else {
	    InputStream inputStream = null;
	    try {
		inputStream = response.getEntity().getContent();
		 handleSuccess(status.getStatusCode(), inputStream);
	    } catch (IllegalStateException e) {
		handleFailure(e);
	    } catch (IOException e) {
		handleFailure(e);
	    } finally{
		if(inputStream != null){
		    try {
			inputStream.close();
		    } catch (IOException e) {
			handleFailure(e);
		    }
		}
	    }
	   
	}
    }
    
    @Override
    protected void sendFailureMessage(Throwable e, String responseBody) {
	handleFailure(e);
    }

    @Override
    protected void sendFailureMessage(Throwable e, byte[] responseBody) {
	handleFailure(e);
    }

    @Override
    protected void sendFinishMessage() {
	//Client code will handle propagating response back 
    }


    @Override
    protected void sendMessage(Message msg) {
	//Client code will handle propagating response back 
    }
    
    public abstract void handleSuccess(int statusCode, InputStream inputStream);
    public abstract void handleFailure(Throwable e);



}
