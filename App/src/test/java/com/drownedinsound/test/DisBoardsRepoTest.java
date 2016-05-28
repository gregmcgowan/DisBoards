package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepoImpl;
import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.data.network.UrlConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 09/12/15.
 */
public class DisBoardsRepoTest {

    public static final String BOARD_POST_ID = "4471118";

    public static final String AUTH_TOKEN = "authToken";

    @Mock
    DisApiClient disApiClient;

    @Mock
    DisBoardsLocalRepo disBoardsLocalRepo;

    @Mock
    UserSessionRepo userSessionRepo;

    DisBoardRepoImpl disBoardRepo;

    private List<BoardPostSummary> testBoardPostSummaries;

    private CountDownLatch countDownLatch;

    private BoardPostList boardPostList;

    private BoardPost expectedBoardPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        disBoardRepo = new DisBoardRepoImpl(disApiClient, disBoardsLocalRepo,
                userSessionRepo);

        boardPostList = new BoardPostList(BoardListTypes.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 0, 19, 0);

        BoardPostSummary boardPostSummary = new BoardPostSummary();
        boardPostSummary.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
        boardPostSummary.setTitle(BoardPostTestData.BOARD_POST_TITLE);
        boardPostSummary.setNumberOfReplies(BoardPostTestData.BOARD_POST_NUMBER_OF_COMMENTS);

        testBoardPostSummaries = new ArrayList<>();
        testBoardPostSummaries.add(boardPostSummary);

        expectedBoardPost = new BoardPost();
        expectedBoardPost.setBoardPostID("4471118");
        expectedBoardPost.setAuthorUsername("shitty_zombies");
        expectedBoardPost.setTitle("Charlie Brooker&#x27;s 2015 Wipe");
        expectedBoardPost.setContent("<p>Christ, that was bleak, wasn&#x27;t it?</p>");
        expectedBoardPost.setDateOfPost("22:55, 30 December '15");
        expectedBoardPost.setBoardListTypeID(BoardListTypes.SOCIAL);
        expectedBoardPost.setNumberOfReplies(5);
    }


    @Test
    public void testLogin() throws Exception {
        LoginResponse loginResponse = new LoginResponse("token");

        when(disApiClient.loginUser("username", "password"))
                .thenReturn(Observable.just(loginResponse));

        disBoardRepo.loginUser("username", "password")
                .subscribeOn(Schedulers.immediate())
                .subscribe(new Action1<LoginResponse>() {
                    @Override
                    public void call(LoginResponse loginResponse) {

                    }
                });

        verify(disApiClient).loginUser("username", "password");
        verify(userSessionRepo).setAuthenticityToken("token");
    }

    @Test
    public void testGetListFromNetwork() throws Exception {
        int page = 1;

        when(disBoardsLocalRepo.getBoardPostList(BoardListTypes.MUSIC))
                .thenReturn(Observable.just(boardPostList));

        when(disApiClient.getBoardPostSummaryList(BoardListTypes.MUSIC,
                boardPostList.getUrl(), 1))
                .thenReturn(Observable.just(testBoardPostSummaries));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, page, true)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<List<BoardPostSummary>>() {
                    @Override
                    public void call(List<BoardPostSummary> boardPosts) {
                        BoardPostSummary expected = testBoardPostSummaries.get(0);
                        BoardPostSummary actual = boardPosts.get(0);
                        AssertUtils.assertBoardPostSummary(expected, actual);
                        countDownLatch.countDown();
                    }
                });
        verify(disApiClient).getBoardPostSummaryList(BoardListTypes.MUSIC,
                boardPostList.getUrl(), page);
        countDownLatch.await();
    }

    @Test
    public void testGetListCached() throws Exception {
        int page = 1;

        boardPostList.setLastFetchedMs(System.currentTimeMillis() - (60 * 1000));
        boardPostList.setBoardPostSummaries(testBoardPostSummaries);

        when(disBoardsLocalRepo.getBoardPostList(BoardListTypes.MUSIC))
                .thenReturn(Observable.just(boardPostList));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, page, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<List<BoardPostSummary>>() {
                    @Override
                    public void call(List<BoardPostSummary> boardPosts) {
                        BoardPostSummary expected = testBoardPostSummaries.get(0);
                        BoardPostSummary actual = boardPosts.get(0);
                        AssertUtils.assertBoardPostSummary(expected, actual);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }

    @Test
    public void testGetListNetworkError() throws Exception {
        int page = 1;
        boardPostList.setBoardPostSummaries(testBoardPostSummaries);

        when(disApiClient.getBoardPostSummaryList(BoardListTypes.MUSIC,
                boardPostList.getUrl(), 1)).thenReturn(Observable.create(
                new Observable.OnSubscribe<List<BoardPostSummary>>() {
                    @Override
                    public void call(Subscriber<? super List<BoardPostSummary>> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        when(disBoardsLocalRepo.getBoardPostList(BoardListTypes.MUSIC)).thenReturn(Observable.just(
                boardPostList));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, page, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<List<BoardPostSummary>>() {
                    @Override
                    public void call(List<BoardPostSummary> boardPosts) {
                        BoardPostSummary expected = testBoardPostSummaries.get(0);
                        BoardPostSummary actual = boardPosts.get(0);
                        AssertUtils.assertBoardPostSummary(expected, actual);
                        countDownLatch.countDown();
                    }
                });
        verify(disApiClient).getBoardPostSummaryList(BoardListTypes.MUSIC,
                boardPostList.getUrl(), page);
        countDownLatch.await();
    }

    @Test
    public void testGetBoardPostForce() throws Exception {
        when(disApiClient.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID))
                .thenReturn(Observable.just(expectedBoardPost));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID, true)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();

        verify(disApiClient, times(1)).getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID);
       // verify(disBoardsLocalRepo, never()).getBoardPost(BOARD_POST_ID);
    }

    @Test
    public void testGetBoardPostCached() throws Exception {
        expectedBoardPost.setLastFetchedTime(System.currentTimeMillis());
        when(disBoardsLocalRepo.getBoardPost(BOARD_POST_ID))
                .thenReturn(Observable.just(expectedBoardPost));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();

        verify(disApiClient, never()).getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID);
        verify(disBoardsLocalRepo, times(1)).getBoardPost(BOARD_POST_ID);
    }

    @Test
    public void testGetBoardPostStaleData() throws Exception {
        expectedBoardPost.setLastFetchedTime(System.currentTimeMillis()
                - (20L * 60L * 1000L));

        when(disBoardsLocalRepo.getBoardPost(BOARD_POST_ID))
                .thenReturn(Observable.just(expectedBoardPost));

        when(disApiClient.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID))
                .thenReturn(Observable.just(expectedBoardPost));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();

        verify(disApiClient, times(1)).getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID);
        verify(disBoardsLocalRepo, times(2)).getBoardPost(BOARD_POST_ID);
    }


    @Test
    public void testGetBoardNetworkError() throws Exception {
        expectedBoardPost.setLastFetchedTime(System.currentTimeMillis()
                - (20L * 60L * 1000L));

        when(disBoardsLocalRepo.getBoardPost(BOARD_POST_ID))
                .thenReturn(Observable.just(expectedBoardPost));

        when(disApiClient.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID))
                .thenReturn(Observable.create(new Observable.OnSubscribe<BoardPost>() {
                    @Override
                    public void call(Subscriber<? super BoardPost> subscriber) {
                        subscriber.onError(new Exception("Exception"));
                    }
                }));
        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();

        verify(disApiClient, times(1)).getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID);
        verify(disBoardsLocalRepo, times(2)).getBoardPost(BOARD_POST_ID);
    }

    @Test
    public void testNoCachedValue() throws Exception {
        when(disBoardsLocalRepo.getBoardPost(BOARD_POST_ID))
                .thenReturn(Observable.create(new Observable.OnSubscribe<BoardPost>() {
                    @Override
                    public void call(Subscriber<? super BoardPost> subscriber) {
                        BoardPost nullPost = null;
                        subscriber.onNext(nullPost);
                        subscriber.onCompleted();
                    }
                }));

        when(disApiClient.getBoardPost(BoardListTypes.SOCIAL,BOARD_POST_ID))
                .thenReturn(Observable.just(expectedBoardPost));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
        verify(disApiClient, times(1)).getBoardPost(BoardListTypes.SOCIAL, BOARD_POST_ID);
        verify(disBoardsLocalRepo, times(2)).getBoardPost(BOARD_POST_ID);

    }

    //TODO test post a comment


    @Test
    public void testThisAComment() throws Exception {
        when(disApiClient
                .thisAComment(BoardListTypes.SOCIAL, BOARD_POST_ID, "COMMENTID", AUTH_TOKEN))
                .thenReturn(Observable.just(expectedBoardPost));
        when(userSessionRepo.getAuthenticityToken()).thenReturn(AUTH_TOKEN);

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.thisAComment(BoardListTypes.SOCIAL, BOARD_POST_ID, "COMMENTID")
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();

        verify(disApiClient, times(1)).thisAComment(BoardListTypes.SOCIAL, BOARD_POST_ID,
                "COMMENTID", AUTH_TOKEN);
        verify(userSessionRepo, times(1)).getAuthenticityToken();
        verify(disBoardsLocalRepo, times(1)).setBoardPost(expectedBoardPost);

    }

    @Test
    public void testAddANewPost() throws Exception {
        when(disApiClient.addNewPost(BoardListTypes.MUSIC, "New title", "New Content", AUTH_TOKEN,
                String.valueOf(boardPostList.getSectionId())))
                .thenReturn(Observable.just(expectedBoardPost));
        when(disBoardsLocalRepo.getBoardPostList(BoardListTypes.MUSIC))
                .thenReturn(Observable.just(boardPostList));
        when(userSessionRepo.getAuthenticityToken()).thenReturn(AUTH_TOKEN);

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.addNewPost(BoardListTypes.MUSIC, "New title", "New Content")
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        AssertUtils.assertBoardPost(expectedBoardPost, boardPost);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();

        verify(disApiClient,times(1)).addNewPost(BoardListTypes.MUSIC, "New title", "New Content",
                AUTH_TOKEN, String.valueOf(boardPostList.getSectionId()));
        verify(userSessionRepo, times(1)).getAuthenticityToken();
        verify(disBoardsLocalRepo, times(1)).getBoardPostList(BoardListTypes.MUSIC);
        verify(disBoardsLocalRepo, times(1)).setBoardPost(expectedBoardPost);
        verify(disBoardsLocalRepo, times(1)).setBoardPostList(boardPostList);

    }


}
