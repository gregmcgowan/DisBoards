package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepoImpl;
import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.data.network.UrlConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 09/12/15.
 */
public class DisBoardsRepoTest {

    @Mock
    DisApiClient disApiClient;

    @Mock
    DisBoardsLocalRepo disBoardsLocalRepo;

    @Mock
    UserSessionRepo userSessionRepo;

    DisBoardRepoImpl disBoardRepo;

    private List<BoardPost> testBoardPosts;

    private CountDownLatch countDownLatch;

    private Board board;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        disBoardRepo = new DisBoardRepoImpl(disApiClient, disBoardsLocalRepo,
                userSessionRepo);

        board = new Board(BoardType.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 19, 0);

        BoardPost boardPost = new BoardPost();
        boardPost.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
        boardPost.setDateOfPost(BoardPostTestData.BOARD_POST_DATE_TIME);
        boardPost.setTitle(BoardPostTestData.BOARD_POST_TITLE);
        boardPost.setNumberOfReplies(BoardPostTestData.BOARD_POST_NUMBER_OF_COMMENTS);
        boardPost.setContent(BoardPostTestData.BOARD_POST_CONTENT);

        testBoardPosts = new ArrayList<>();
        testBoardPosts.add(boardPost);
    }

    @Test
    public void testLogin() throws Exception {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAuthenticationToken("token");

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
        String tag = "TAG";
        int page = 1;

        when(disApiClient.getBoardPostSummaryList(any(Board.class), 1))
                .thenReturn(Observable.just(testBoardPosts));
        when(disBoardsLocalRepo.getBoard(BoardType.MUSIC)).thenReturn(Observable.just(board));
        when(disBoardsLocalRepo.getBoardPosts(BoardType.MUSIC)).thenReturn(Observable.just(
                Collections.<BoardPost>emptyList()));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPostSummaryList(BoardType.MUSIC, tag, page, true)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<List<BoardPost>>() {
                    @Override
                    public void call(List<BoardPost> boardPosts) {
                        BoardPost expected = testBoardPosts.get(0);
                        BoardPost actual = boardPosts.get(0);
                        AssertUtils.assertBoardPost(expected, actual);
                        countDownLatch.countDown();
                    }
                });
        verify(disApiClient).getBoardPostSummaryList(any(Board.class), page);
        countDownLatch.await();
    }

    @Test
    public void testGetListCached() throws Exception {
        String tag = "TAG";
        int page = 1;

        board.setLastFetchedTime(System.currentTimeMillis() - (60 * 1000));

        when(disBoardsLocalRepo.getBoard(BoardType.MUSIC)).thenReturn(Observable.just(board));
        when(disBoardsLocalRepo.getBoardPosts(BoardType.MUSIC)).thenReturn(Observable.just(
                testBoardPosts));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPostSummaryList(BoardType.MUSIC, tag, page, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<List<BoardPost>>() {
                    @Override
                    public void call(List<BoardPost> boardPosts) {
                        BoardPost expected = testBoardPosts.get(0);
                        BoardPost actual = boardPosts.get(0);
                        AssertUtils.assertBoardPost(expected, actual);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }

    @Test
    public void testGetListNetworkError() throws Exception{
        String tag = "TAG";
        int page = 1;

        when(disApiClient.getBoardPostSummaryList(any(Board.class), 1)).thenReturn(Observable.create(
                new Observable.OnSubscribe<List<BoardPost>>() {
                    @Override
                    public void call(Subscriber<? super List<BoardPost>> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));
        when(disBoardsLocalRepo.getBoard(BoardType.MUSIC)).thenReturn(Observable.just(board));
        when(disBoardsLocalRepo.getBoardPosts(BoardType.MUSIC)).thenReturn(Observable.just(
                testBoardPosts));

        countDownLatch = new CountDownLatch(1);

        disBoardRepo.getBoardPostSummaryList(BoardType.MUSIC, tag, page, false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<List<BoardPost>>() {
                    @Override
                    public void call(List<BoardPost> boardPosts) {
                        BoardPost expected = testBoardPosts.get(0);
                        BoardPost actual = boardPosts.get(0);
                        AssertUtils.assertBoardPost(expected, actual);
                        countDownLatch.countDown();
                    }
                });
        verify(disApiClient).getBoardPostSummaryList(any(Board.class), page);
        countDownLatch.await();
    }

}
