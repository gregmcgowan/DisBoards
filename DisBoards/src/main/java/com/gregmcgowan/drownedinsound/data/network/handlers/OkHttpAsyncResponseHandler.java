package com.gregmcgowan.drownedinsound.data.network.handlers;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
public abstract class OkHttpAsyncResponseHandler implements Callback{

    private boolean updateUI;

    @Override
    public void onFailure(Request request, IOException e) {
        handleFailure(request,e);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        int responseCode = response.code();
        if (responseCode >= 300) {
           handleFailure(response.request(), new IOException("Response Code "+responseCode));
        } else {
            try {
                handleSuccess(response, new GZIPInputStream(response.body().byteStream()));
            } catch (IOException ioe){
                handleFailure(response.request(),ioe);
            } catch (IllegalStateException e) {
                handleFailure(response.request(),e);
            }
        }
    }

    public boolean isUpdateUI() {
        return updateUI;
    }

    public void setUpdateUI(boolean updateUI) {
        this.updateUI = updateUI;
    }

    public abstract void handleSuccess(Response response, InputStream inputStream) throws  IOException;
    public abstract void handleFailure(Request request, Throwable throwable);
}
