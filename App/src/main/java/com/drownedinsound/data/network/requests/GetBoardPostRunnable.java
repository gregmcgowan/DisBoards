package com.drownedinsound.data.network.requests;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.network.handlers.OkHttpAsyncResponseHandler;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;


import timber.log.Timber;

/**
 * Created by gregmcgowan on 30/11/14.
 */
public class GetBoardPostRunnable extends BaseRunnable {

    private String boardPostUrl;

    public GetBoardPostRunnable(
            OkHttpAsyncResponseHandler responseHandler,
            OkHttpClient okHttpClient, String boardPostUrl) {
        super(responseHandler, okHttpClient);
        this.boardPostUrl = boardPostUrl;
    }

    @Override
    public void run() {
        if (DisBoardsConstants.DEBUG) {
            Timber.d("Going to request = " + boardPostUrl);
        }
        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.get().url(boardPostUrl)
                .headers(headerBuilder.build()).build();
        makeRequest(request);
    }
}
