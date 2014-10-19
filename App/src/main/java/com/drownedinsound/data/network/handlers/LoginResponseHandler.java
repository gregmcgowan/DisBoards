package com.drownedinsound.data.network.handlers;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.data.parser.streaming.HtmlConstants;
import com.drownedinsound.events.LoginResponseEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class LoginResponseHandler extends OkHttpAsyncResponseHandler {

    private static final String AUTHENTICITY_TOKEN_NAME = "csrf-token";

    public LoginResponseHandler(Context context) {
        super(context);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        try {
            String url = response.request().urlString();
            Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Login response url " + url);

            boolean logInSuccess = UrlConstants.SOCIAL_URL.equals(url);
            if (logInSuccess) {
                getAuthToken(response.body().byteStream());
            }

            eventBus.post(new LoginResponseEvent(logInSuccess));
        } catch (IOException ioe) {
            eventBus.post(new LoginResponseEvent(false));
        }
    }

    private void getAuthToken(InputStream inputStream) throws IOException {
        StreamedSource streamedSource = new StreamedSource(inputStream);
        for (Segment segment : streamedSource) {
            if (segment instanceof Tag) {
                Tag tag = (Tag) segment;
                String tagName = tag.getName();
                if (HtmlConstants.META.equals(tagName)) {
                    String metaString = tag.toString();
                    if (metaString.contains(AUTHENTICITY_TOKEN_NAME)) {
                        Attributes attributes = tag.parseAttributes();
                        if (attributes != null) {
                            String authToken = attributes.getValue("content");
                            userSessionManager.setAuthenticityToken(authToken);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        eventBus.post(new LoginResponseEvent(false));
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        //Do nothing
    }
}