package com.drownedinsound.data.network.requests;

import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;


/**
 * Created by gregmcgowan on 30/11/14.
 */
public class LoginRunnable extends BaseRunnable {

    private String username;

    private String password;

    public LoginRunnable(OkHttpClient okHttpClient, LoginResponseHandler handler, String username,
            String password) {
        super(handler, okHttpClient);
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        RequestBody requestBody = new FormEncodingBuilder().add("user_session[username]", username)
                .add("user_session[password]", password)
                .add("user_session[remember_me]", "1")
                .add("return_to", UrlConstants.SOCIAL_URL)
                .add("commit", "Go!*").build();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(requestBody).url(UrlConstants.LOGIN_URL).build();

        makeRequest(request);
    }
}
