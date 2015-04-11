package com.drownedinsound.data.network.requests;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.data.network.handlers.OkHttpAsyncResponseHandler;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

/**
 * Created by gregmcgowan on 07/12/14.
 */
public class AddANewPostRunnable extends BaseRunnable {

    private Board board;

    private String title;

    private String content;

    private String authToken;

    public AddANewPostRunnable(
            OkHttpAsyncResponseHandler responseHandler,
            OkHttpClient okHttpClient, Board board, String title,
            String content, String authToken) {
        super(responseHandler, okHttpClient);
        this.board = board;
        this.title = title;
        this.content = content;
        this.authToken = authToken;
    }

    @Override
    public void run() {
        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        headerBuilder.add("Referer", board.getUrl());
        RequestBody requestBody = new FormEncodingBuilder().add("section_id", String.valueOf(board
                .getSectionId()))
                .add("topic[title]", title)
                .add("topic[content_raw]", content)
                .add("topic[sticky]", "0")
                .add("authenticity_token", authToken).build();
        Request.Builder requestBuilder = new Request.Builder();

        Request request = requestBuilder.post(requestBody).headers(headerBuilder.build())
                .url(UrlConstants.NEW_POST_URL).build();
        makeRequest(request);
    }
}
