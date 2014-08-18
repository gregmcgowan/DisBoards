package com.gregmcgowan.drownedinsound.data.network.handlers;

import android.content.Context;
import android.util.Log;

import com.gregmcgowan.drownedinsound.core.DisBoardsApp;
import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.UserSessionManager;
import com.gregmcgowan.drownedinsound.data.network.UrlConstants;
import com.gregmcgowan.drownedinsound.data.parser.streaming.HtmlConstants;
import com.gregmcgowan.drownedinsound.events.LoginResponseEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoginResponseHandler extends OkHttpAsyncResponseHandler {

    private static final String AUTHENTICITY_TOKEN_NAME = "csrf-token";

    @Inject
    UserSessionManager userSessionManager;

    public LoginResponseHandler(Context context) {
        DisBoardsApp disBoardsApp = DisBoardsApp.getApplication(context);
        disBoardsApp.inject(this);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        String url = response.request().urlString();
        Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Login response url " + url);

        boolean logInSuccess = UrlConstants.SOCIAL_URL.equals(url);
        if(logInSuccess) {
            getAuthToken(inputStream);
        }

        EventBus.getDefault().post(new LoginResponseEvent(logInSuccess));
    }

    private void getAuthToken(InputStream inputStream) throws IOException {
        StreamedSource streamedSource = new StreamedSource(inputStream);
        for (Segment segment : streamedSource) {
            if (segment instanceof Tag) {
                Tag tag = (Tag) segment;
                String tagName = tag.getName();
                if(HtmlConstants.META.equals(tagName)){
                    String metaString = tag.toString();
                    if(metaString.contains(AUTHENTICITY_TOKEN_NAME)){
                        Attributes attributes = tag.parseAttributes();
                        if(attributes != null) {
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
        EventBus.getDefault().post(new LoginResponseEvent(false));
    }
};