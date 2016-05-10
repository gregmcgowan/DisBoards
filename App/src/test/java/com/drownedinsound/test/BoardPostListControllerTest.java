package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.ui.addPost.AddPostUI;
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
    DisBoardRepo disBoardRepo;

    @Mock
    Display display;

    @Mock
    AddPostUI newPostUI;

    private BoardPostListController boardPostListController;

    private BoardPostList boardPostListInfo;

    private @BoardPostList.BoardPostListType String boardListType;

    private List<BoardPostSummary> boardPosts;

    private BoardPost expectedBoardPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        boardPostListController = new BoardPostListController(disBoardRepo, Schedulers.immediate(),
                Schedulers.immediate());
        boardListType = BoardListTypes.MUSIC;

        boardPostListInfo = new BoardPostList(BoardListTypes.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 0, 19,0);

        BoardPostSummary boardPost = new BoardPostSummary();
        boardPost.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
        boardPost.setTitle(BoardPostTestData.BOARD_POST_TITLE);
        boardPost.setNumberOfReplies(BoardPostTestData.BOARD_POST_NUMBER_OF_COMMENTS);

        boardPosts = new ArrayList<>();
        boardPosts.add(boardPost);

        boardPostListInfo.setBoardPostSummaries(boardPosts);

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
    public void testGetListFirstPageSuccessfully() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, 1, true))
                .thenReturn(Observable.just(boardPosts));

        boardPostListController.attachUi(boardPostListUi);

        boardPostListController.requestBoardSummaryPage(boardPostListUi, boardListType, 1, true);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).setBoardPostSummaries(boardPosts);
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostListController.detachUi(boardPostListUi);
    }


    @Test
    public void testGetListSecondPageSuccessfully() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, 2, true))
                .thenReturn(Observable.just(boardPosts));

        boardPostListController.attachUi(boardPostListUi);

        boardPostListController.requestBoardSummaryPage(boardPostListUi, boardListType, 2, true);

        verify(boardPostListUi).appendBoardPostSummaries(boardPosts);

        boardPostListController.detachUi(boardPostListUi);
    }

    @Test
    public void testGetListFirstPageError() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, 1 , true))
                .thenReturn(Observable.create(new Observable.OnSubscribe<List<BoardPostSummary>>() {
                    @Override
                    public void call(Subscriber<? super List<BoardPostSummary>> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        boardPostListController.attachUi(boardPostListUi);

        boardPostListController.requestBoardSummaryPage(boardPostListUi, BoardListTypes.MUSIC, 1,
                true);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).showErrorView();
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostListController.detachUi(boardPostListUi);
    }

    @Test
    public void testStopLoadingOnDetach() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        boardPostListController.attachUi(boardPostListUi);
        boardPostListController.detachUi(boardPostListUi);

        verify(boardPostListUi).showLoadingProgress(false);
    }

    @Test
    public void testLoadListOnAttachToParent() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(boardPostListUi.getID()).thenReturn(1);
        when(boardPostListParentUi.getID()).thenReturn(2);

        when(boardPostListUi.isDisplayed()).thenReturn(true);

        when(disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, 1, false))
                .thenReturn(Observable.just(boardPosts));

        List<BoardPostList> boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(boardPostListInfo);

        when(disBoardRepo.getAllBoardPostLists())
                .thenReturn(Observable.just(boardPostListInfos));

        boardPostListController.attachUi(boardPostListParentUi);
        boardPostListController.attachUi(boardPostListUi);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).setBoardPostSummaries(boardPosts);
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostListController.detachUi(boardPostListUi);
        boardPostListController.detachUi(boardPostListParentUi);
    }


    @Test
    public void testBoardPostSelected() {
        BoardPostSummary boardPostSummary = boardPosts.get(0);

        when(disBoardRepo.setBoardPostSummary(boardPostSummary))
                .thenReturn(Observable.create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        subscriber.onCompleted();
                    }
                }));
        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(boardPostListUi);


        boardPostListController.handleBoardPostSummarySelected(boardPostListUi, boardPostSummary);

        @BoardPostList.BoardPostListType String boardListType = boardPostSummary.getBoardListTypeID();
        verify(display).showBoardPost(boardListType,
                boardPostSummary.getBoardPostID());
        verify(disBoardRepo).setBoardPostSummary(boardPostSummary);
    }




}
