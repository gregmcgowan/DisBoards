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
public class GetBoardPostSummaryListRunnable extends BaseRunnable {

    private String boardUrl;

    public GetBoardPostSummaryListRunnable(
            OkHttpAsyncResponseHandler responseHandler,
            OkHttpClient okHttpClient, String boardUrl) {
        super(responseHandler, okHttpClient);
        this.boardUrl = boardUrl;
    }

    @Override
    public void run() {
        if (DisBoardsConstants.DEBUG) {
            Timber.d("Going to request = " + boardUrl);
        }

        Request.Builder requestBuilder = new Request.Builder();
        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        Request request = requestBuilder.get().url(boardUrl)
                .headers(headerBuilder.build()).build();

        makeRequest(request);
    }
}
