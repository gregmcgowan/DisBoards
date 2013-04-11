package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Message;

/**
 * Saves the response to a file but does not call the handle methods on
 * the main thread
 * 
 * @author Greg
 *
 */
public abstract class FileAsyncBackgroundThreadHttpResponseHandler extends
	AsyncHttpResponseHandler {

    private File mFile;

    public FileAsyncBackgroundThreadHttpResponseHandler(File file) {
	this.mFile = file;
    }

  
    @Override
    void sendResponseMessage(HttpResponse response) {
	StatusLine status = response.getStatusLine();

	try {
	    FileOutputStream buffer = new FileOutputStream(this.mFile);
	    InputStream is = response.getEntity().getContent();

	    int nRead;
	    byte[] data = new byte[16384];

	    while ((nRead = is.read(data, 0, data.length)) != -1)
		buffer.write(data, 0, nRead);

	    buffer.flush();
	    buffer.close();

	} catch (IOException e) {
	    handleFailure(e, this.mFile);
	}

	if (status.getStatusCode() >= 300) {
	    handleFailure(new HttpResponseException(
		    status.getStatusCode(), status.getReasonPhrase()),
		    this.mFile);
	} else {
	    handleSuccess(status.getStatusCode(), this.mFile);
	}
    }
    
    
    protected void deleteFile(){
	if(mFile != null){
	    mFile.delete();
	}
    }
    
    public abstract void handleSuccess(int statusCode, File file);
    public abstract void handleFailure(Throwable e, File response);


    @Override
    protected void sendFailureMessage(Throwable e, String responseBody) {
	handleFailure(e,null);
    }


    @Override
    protected void sendFailureMessage(Throwable e, byte[] responseBody) {
	handleFailure(e,null);
    }


    @Override
    protected void sendFinishMessage() {

    }


    @Override
    protected void sendMessage(Message msg) {

    }
    
    
    
    

}