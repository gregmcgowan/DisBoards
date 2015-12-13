package com.drownedinsound.test;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.test.login.FakeDisRepo;
import com.drownedinsound.ui.postList.BoardPostListController;
import com.drownedinsound.ui.postList.BoardPostListParentUi;
import com.drownedinsound.ui.postList.BoardPostListUi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public class BoardPostListControllerTest {

    @Mock
    BoardPostListUi boardPostListUi;

    @Mock
    BoardPostListParentUi boardPostListParentUi;

    @Mock
    FakeDisRepo fakeDisRepo;

    private BoardPostListController boardPostController;

    private Board board;

    private BoardType boardType;

    private List<BoardPost> boardPosts;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        boardPostController = new BoardPostListController(fakeDisRepo, Schedulers.immediate(),
                Schedulers.immediate());
        boardType = BoardType.MUSIC;

        board = new Board(BoardType.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 19, 0);

        BoardPost boardPost = new BoardPost();
        boardPost.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
        boardPost.setDateOfPost(BoardPostTestData.BOARD_POST_DATE_TIME);
        boardPost.setTitle(BoardPostTestData.BOARD_POST_TITLE);
        boardPost.setNumberOfReplies(BoardPostTestData.BOARD_POST_NUMBER_OF_COMMENTS);
        boardPost.setContent(BoardPostTestData.BOARD_POST_CONTENT);

        boardPosts = new ArrayList<>();
        boardPosts.add(boardPost);
    }

    @Test
    public void testGetListFirstPageSuccessfully() {
        when(boardPostListUi.getBoardType()).thenReturn(boardType);
        when(boardPostListUi.getBoardList()).thenReturn(board);
        when(fakeDisRepo.getBoardPostSummaryList(any(BoardType.class), anyObject(), anyInt(), anyBoolean()))
                .thenReturn(Observable.just(boardPosts));

        boardPostController.attachUi(boardPostListUi);

        boardPostController.requestBoardSummaryPage(boardPostListUi, boardType, 1, true);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).setBoardPosts(boardPosts);
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostController.detachUi(boardPostListUi);
    }


    @Test
    public void testGetListSecondPageSuccessfully() {
        when(boardPostListUi.getBoardType()).thenReturn(boardType);
        when(boardPostListUi.getBoardList()).thenReturn(board);

        when(fakeDisRepo.getBoardPostSummaryList(any(BoardType.class), anyObject(), anyInt(), anyBoolean()))
                .thenReturn(Observable.just(boardPosts));

        boardPostController.attachUi(boardPostListUi);

        boardPostController.requestBoardSummaryPage(boardPostListUi, boardType, 2, true);

        verify(boardPostListUi).appendBoardPosts(boardPosts);

        boardPostController.detachUi(boardPostListUi);
    }

    @Test
    public void testGetListFirstPageError() {
        when(boardPostListUi.getBoardType()).thenReturn(boardType);
        when(boardPostListUi.getBoardList()).thenReturn(board);

        when(fakeDisRepo
                .getBoardPostSummaryList(any(BoardType.class), anyObject(), anyInt(), anyBoolean()))
                .thenReturn(Observable.create(new Observable.OnSubscribe<List<BoardPost>>() {
                    @Override
                    public void call(Subscriber<? super List<BoardPost>> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        boardPostController.attachUi(boardPostListUi);

        boardPostController.requestBoardSummaryPage(boardPostListUi, boardType, 1, true);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).showErrorView();
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostController.detachUi(boardPostListUi);
    }

    @Test
    public void testStopLoadingOnDetach() {
        when(boardPostListUi.getBoardType()).thenReturn(boardType);

        boardPostController.attachUi(boardPostListUi);
        boardPostController.detachUi(boardPostListUi);

        verify(boardPostListUi).showLoadingProgress(false);
    }

    @Test
    public void testLoadListOnAttachToParent() {
        when(boardPostListParentUi.boardPostListShown(boardPostListUi)).thenReturn(true);
        when(boardPostListUi.getBoardList()).thenReturn(board);
        when(boardPostListUi.getBoardType()).thenReturn(boardType);

        when(boardPostListUi.getId()).thenReturn(1);
        when(boardPostListParentUi.getId()).thenReturn(2);

        when(fakeDisRepo
                .getBoardPostSummaryList(any(BoardType.class), anyObject(), anyInt(),
                        anyBoolean()))
                .thenReturn(Observable.just(boardPosts));

        boardPostController.attachUi(boardPostListParentUi);
        boardPostController.attachUi(boardPostListUi);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).setBoardPosts(boardPosts);
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostController.detachUi(boardPostListUi);
        boardPostController.detachUi(boardPostListParentUi);
    }
}
