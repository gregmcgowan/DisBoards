package com.drownedinsound.data.network.requests;

import com.drownedinsound.data.network.handlers.OkHttpAsyncResponseHandler;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import timber.log.Timber;

/**
 * Created by gregmcgowan on 07/12/14.
 */
public class ThisACommentRunnable extends BaseRunnable {

    private String boardPostUrl;

    private String commentId;

    public ThisACommentRunnable(String postUrl, String commentId,
            OkHttpAsyncResponseHandler responseHandler,
            OkHttpClient okHttpClient) {
        super(responseHandler, okHttpClient);
        this.boardPostUrl = postUrl;
        this.commentId = commentId;
    }

    @Override
    public void run() {
        String fullUrl = boardPostUrl + "/" + commentId + "/this";
        Timber.d("Going to this with  =" + fullUrl);

        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.get().url(fullUrl)
                .headers(headerBuilder.build()).build();
        makeRequest(request);
    }
}
