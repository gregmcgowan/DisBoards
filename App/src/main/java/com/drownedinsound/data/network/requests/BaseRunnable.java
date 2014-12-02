package com.drownedinsound.data.network.requests;

import com.drownedinsound.data.network.handlers.OkHttpAsyncResponseHandler;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.lang.ref.WeakReference;


/**
 * Created by gregmcgowan on 30/11/14.
 */
public abstract class BaseRunnable implements Runnable {

    private OkHttpAsyncResponseHandler okHttpAsyncResponseHandler;

    private WeakReference<OkHttpClient> httpClientWeakReference;

    public BaseRunnable(OkHttpAsyncResponseHandler responseHandler, OkHttpClient okHttpClient){
        this.okHttpAsyncResponseHandler = responseHandler;
        this.httpClientWeakReference = new WeakReference<OkHttpClient>(okHttpClient);
    }

    protected void makeRequest(Request request){
        if(httpClientWeakReference != null) {
            if( httpClientWeakReference.get() != null){
                httpClientWeakReference.get().newCall(request).enqueue(okHttpAsyncResponseHandler);
            }
        }
    }

    protected Headers.Builder getMandatoryDefaultHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Cache-Control", "max-age=0");
        headerBuilder.add("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
        headerBuilder
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headerBuilder.add("Accept-Encoding", "gzip,deflate,sdch");
        headerBuilder.add("Accept-Language", "en-US,en;q=0.8,en-GB;q=0.6");
        headerBuilder.add("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
        return headerBuilder;
    }


}
