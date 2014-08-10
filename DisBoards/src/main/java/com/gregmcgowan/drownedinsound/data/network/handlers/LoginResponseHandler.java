package com.gregmcgowan.drownedinsound.data.network.handlers;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.events.LoginResponseEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

public class LoginResponseHandler extends OkHttpAsyncResponseHandler {

    private static final String LOGIN_IDENTIFIER = "LOGIN";

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        String url = response.request().urlString();
        Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Login response url " + url);
        EventBus.getDefault().post(new LoginResponseEvent(true));
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        EventBus.getDefault().post(new LoginResponseEvent(false));
    }
};