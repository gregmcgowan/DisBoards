package com.drownedinsound.test;

import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.data.network.UrlConstants;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import android.app.Application;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by gregmcgowan on 09/12/15.
 */
public class DisApiTest {


    private DisApiClient disApiClient;

    @Mock
    Application application;

    @Mock
    UserSessionRepo userSessionRepo;

    CountDownLatch countDownLatch;

    @Before
    public void setup() {
        OkHttpClient okHttpClient = new OkHttpClient();
        disApiClient = new DisApiClient(application,okHttpClient,userSessionRepo);
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();


        mockWebServer.start();

        final String token ="test";

        HttpUrl base = mockWebServer.url("");
        System.out.println("base url "+base.toString());

        mockWebServer.enqueue(new MockResponse().setBody("blah blah").addHeader("Location",
                base + UrlConstants.LOGIN_PATH).setResponseCode(302));

        mockWebServer.enqueue(new MockResponse().setBody("blah blah"));

        disApiClient.setBaseUrl(base.toString());

        countDownLatch = new CountDownLatch(1);

        disApiClient.loginUser("username","password")
                .subscribeOn(Schedulers.immediate())
                .subscribe(new Action1<LoginResponse>() {
                    @Override
                    public void call(LoginResponse loginResponse) {
                        Assert.assertEquals(token, loginResponse.getAuthenticationToken());
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
        mockWebServer.shutdown();
    }
}
