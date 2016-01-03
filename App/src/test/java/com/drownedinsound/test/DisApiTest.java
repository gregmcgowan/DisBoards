package com.drownedinsound.test;

import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.app.Application;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

/**
 * Created by gregmcgowan on 09/12/15.
 */
public class DisApiTest {


    private DisApiClient disApiClient;

    @Mock
    Application application;

    @Mock
    UserSessionRepo userSessionRepo;

    @Mock
    DisWebPageParser disWebPageParser;

    CountDownLatch countDownLatch;

    private List<BoardPostSummary> testBoardPostSummaires;

    private BoardPostList boardPostListInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        OkHttpClient okHttpClient = new OkHttpClient();
        disApiClient = new DisApiClient(application,okHttpClient,disWebPageParser);

        boardPostListInfo = new BoardPostList(BoardListTypes.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 0, 19,0);

        BoardPostSummary boardPost = new BoardPostSummary();
        boardPost.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
        boardPost.setTitle(BoardPostTestData.BOARD_POST_TITLE);
        boardPost.setNumberOfReplies(BoardPostTestData.BOARD_POST_NUMBER_OF_COMMENTS);

        testBoardPostSummaires = new ArrayList<>();
        testBoardPostSummaires.add(boardPost);
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        final String token ="test";
        when(disWebPageParser.getAuthenticationToken(any(InputStream.class))).thenReturn(token);

        MockWebServer mockWebServer = new MockWebServer();

        mockWebServer.start();

        HttpUrl base = mockWebServer.url("");

        mockWebServer.enqueue(new MockResponse().setBody("blah blah").addHeader("Location",
                base + UrlConstants.BOARD_BASE_PATH
                        + UrlConstants.SOCIAL_BOARD_NAME).setResponseCode(302));

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

    @Test
    public void testGetList() throws Exception {
        when(disWebPageParser.parseBoardPostSummaryList(eq(BoardListTypes.MUSIC),
                any(InputStream.class))).thenReturn(
                testBoardPostSummaires);

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("gewgewgewgew"));
        mockWebServer.start();

        HttpUrl base = mockWebServer.url("");
        disApiClient.setBaseUrl(base.toString());

        countDownLatch = new CountDownLatch(1);

        disApiClient.getBoardPostSummaryList(BoardListTypes.MUSIC,
                boardPostListInfo.getUrl(),1)
                .subscribeOn(Schedulers.immediate())
                .subscribe(new Action1<List<BoardPostSummary>>() {
                    @Override
                    public void call(List<BoardPostSummary> boardPosts) {
                        BoardPostSummary expected = testBoardPostSummaires.get(0);
                        BoardPostSummary actual = boardPosts.get(0);
                        AssertUtils.assertBoardPostSummary(expected, actual);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
        mockWebServer.shutdown();
    }
}
