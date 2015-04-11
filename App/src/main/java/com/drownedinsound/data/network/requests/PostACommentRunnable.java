package com.drownedinsound.data.network.requests;

import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.data.network.handlers.OkHttpAsyncResponseHandler;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import android.text.TextUtils;

/**
 * Created by gregmcgowan on 07/12/14.
 */
public class PostACommentRunnable extends BaseRunnable {

    private String boardPostId;

    private String commentId;

    private String title;

    private String content;

    private String authToken;

    public PostACommentRunnable(
            OkHttpAsyncResponseHandler responseHandler,
            OkHttpClient okHttpClient, String boardPostId,
            String commentId, String title, String content, String authToken) {
        super(responseHandler, okHttpClient);

        if (TextUtils.isEmpty(authToken)) {
            throw new IllegalArgumentException("Auth token cannot be null");
        }

        if (TextUtils.isEmpty(boardPostId)) {
            throw new IllegalArgumentException("BoardPostId cannot be null");
        }

        this.boardPostId = boardPostId;
        this.commentId = commentId;
        this.title = title;
        this.content = content;
        this.authToken = authToken;

    }

    @Override
    public void run() {
        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        if (commentId == null) {
            commentId = "";
        }

        RequestBody requestBody = new FormEncodingBuilder()
                .add("comment[commentable_id]", boardPostId)
                .add("comment[title]", title)
                .add("comment[commentable_type]", "Topic")
                .add("comment[content_raw]", content)
                .add("parent_id", commentId)
                .add("authenticity_token", authToken)
                .add("commit", "Post reply").build();
        Request.Builder requestBuilder = new Request.Builder();

        Request request = requestBuilder.post(requestBody).headers(headerBuilder.build())
                .url(UrlConstants.COMMENTS_URL).build();
        makeRequest(request);
    }
}
