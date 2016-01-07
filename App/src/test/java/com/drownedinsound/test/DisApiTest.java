package com.drownedinsound.test;

import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
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
import com.squareup.okhttp.mockwebserver.RecordedRequest;

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
        disApiClient = new DisApiClient(okHttpClient,disWebPageParser);

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

    @Test
    public void testGetBoardPost () throws Exception {
        final BoardPost expectedBoardPost = new BoardPost();
        expectedBoardPost.setBoardPostID("4471118");
        expectedBoardPost.setAuthorUsername("shitty_zombies");
        expectedBoardPost.setTitle("Charlie Brooker&#x27;s 2015 Wipe");
        expectedBoardPost.setContent("<p>Christ, that was bleak, wasn&#x27;t it?</p>");
        expectedBoardPost.setDateOfPost("22:55, 30 December '15");
        expectedBoardPost.setBoardListTypeID(BoardListTypes.SOCIAL);
        expectedBoardPost.setNumberOfReplies(5);

        BoardPostComment initialComment = new BoardPostComment();
        initialComment.setBoardPostID("4471118");
        initialComment.setAuthorUsername("shitty_zombies");
        initialComment.setTitle("Charlie Brooker&#x27;s 2015 Wipe");
        initialComment.setContent("<p>Christ, that was bleak, wasn&#x27;t it?</p>");
        initialComment.setDateAndTime("22:55, 30 December '15");

        BoardPostComment firstComment = new BoardPostComment();
        firstComment.setBoardPostID("4471118");
        firstComment.setDateAndTime("30 Dec '15, 23:01");
        firstComment.setAuthorUsername("-dan-");
        firstComment.setTitle("Cheers, hadn&#x27;t even realised it was on.");
        firstComment.setCommentLevel(0);
        firstComment.setCommentID("8823245");

        BoardPostComment secondComment = new BoardPostComment();
        secondComment.setBoardPostID("4471118");
        secondComment.setDateAndTime("30 Dec '15, 23:01");
        secondComment.setAuthorUsername("BMS1");
        secondComment.setTitle("I&#x27;ve got it taped. I&#x27;ll watch it tomorrow.");
        secondComment.setContent(
                "<p>Usually it's good fun. Hopefully, Barry Shitpeas has some good lines.</p>");
        secondComment.setCommentID("8823246");

        BoardPostComment thirdComment = new BoardPostComment();
        thirdComment.setBoardPostID("4471118");
        thirdComment.setTitle("Had a feeling it would be");
        thirdComment.setContent("<p>Hence why I didn't watch it this time!</p>");
        thirdComment.setAuthorUsername("kiyonemakibi");
        thirdComment.setDateAndTime("30 Dec '15, 23:19");
        thirdComment.setCommentID("8823251");

        BoardPostComment fourthComment = new BoardPostComment();
        fourthComment.setBoardPostID("4471118");
        fourthComment.setTitle("i think he&#x27;s phoning it in now");
        fourthComment.setAuthorUsername("bluto");
        fourthComment.setDateAndTime("31 Dec '15, 03:13");
        fourthComment.setCommentID("8823285");

        BoardPostComment fifthComment = new BoardPostComment();
        fifthComment.setBoardPostID("4471118");
        fifthComment.setTitle("Autofill?");
        fifthComment.setAuthorUsername("Pentago");
        fifthComment.setDateAndTime("31 Dec '15, 09:56");
        fifthComment.setUsersWhoHaveThissed("bluto and ericthethird this'd this");
        fifthComment.setCommentID("8823351");
        fifthComment.setCommentLevel(1);

        List<BoardPostComment> boardPostComments = new ArrayList<>();
        boardPostComments.add(initialComment);
        boardPostComments.add(firstComment);
        boardPostComments.add(secondComment);
        boardPostComments.add(thirdComment);
        boardPostComments.add(fourthComment);
        boardPostComments.add(fifthComment);

        expectedBoardPost.setComments(boardPostComments);

        when(disWebPageParser.parseBoardPost(any(InputStream.class))).thenReturn(expectedBoardPost);

        final MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("gewgewgewgew"));
        mockWebServer.start();

        HttpUrl base = mockWebServer.url("");
        disApiClient.setBaseUrl(base.toString());

        countDownLatch = new CountDownLatch(1);
        disApiClient.getBoardPost(BoardListTypes.SOCIAL,"4471118")
                .subscribeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
        RecordedRequest lastRequest = mockWebServer.takeRequest();

        Assert.assertEquals(lastRequest.getPath(),"/community/boards/social/4471118");
        mockWebServer.shutdown();

    }
}
