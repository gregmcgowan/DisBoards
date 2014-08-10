package com.gregmcgowan.drownedinsound.data.network.handlers;

import android.os.Message;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
public abstract class OkHttpAsyncResponseHandler implements Callback{

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
            InputStream inputStream = null;
            try {
                handleSuccess(response,response.body().byteStream());
            } catch (IOException ioe){
              handleFailure(response.request(),ioe);
            } catch (IllegalStateException e) {
                handleFailure(response.request(),e);
            }

        }
    }


    public abstract void handleSuccess(Response response, InputStream inputStream) throws  IOException;
    public abstract void handleFailure(Request request, Throwable throwable);
}
