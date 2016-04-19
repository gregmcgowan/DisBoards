package com.drownedinsound.test;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.ui.postList.AddPostUI;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public class BoardPostListPostListControllerTest {

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
        when(boardPostListParentUi.getCurrentPageShow()).thenReturn(1);
        when(boardPostListUi.getPageIndex()).thenReturn(1);
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(boardPostListUi.getID()).thenReturn(1);
        when(boardPostListParentUi.getID()).thenReturn(2);

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
    public void testGetListInfo() {
        List<BoardPostList> boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(boardPostListInfo);

        when(disBoardRepo.getAllBoardPostLists())
                .thenReturn(Observable.just(boardPostListInfos));

        when(boardPostListParentUi.getID()).thenReturn(1);

        boardPostListController.uiCreated(boardPostListParentUi);
        boardPostListController.attachUi(boardPostListParentUi);
        boardPostListController.detachUi(boardPostListParentUi);
        boardPostListController.attachUi(boardPostListParentUi);

        verify(disBoardRepo).getAllBoardPostLists();
        verify(boardPostListParentUi,times(1)).setBoardPostLists(boardPostListInfos);

    }
    @Test
    public void testAddNewPostNotLoggedIn() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(false);

        when(boardPostListParentUi.getID()).thenReturn(1);
        when(boardPostListUi.getPageIndex()).thenReturn(1);

        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(boardPostListUi);
        boardPostListController.doNewNewPostAction(1);

        verify(display).showNotLoggedInUI();
    }

    @Test
    public void testAddNewPostLoggedIn() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        when(boardPostListParentUi.getID()).thenReturn(1);
        when(boardPostListUi.getPageIndex()).thenReturn(1);

        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(boardPostListUi);
        boardPostListController.doNewNewPostAction(1);

        verify(display).showNewPostUI(boardPostListUi.getBoardListType());
    }

    @Test
    public void testNewPostValidationEmptyStrings() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(newPostUI);
        boardPostListController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
                "", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_some_content_and_subject);
    }

    @Test
    public void testNewPostValidationEmptyTitle() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(newPostUI);
        boardPostListController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
                "", "Some content");

        verify(display).showErrorMessageDialog(R.string.please_enter_a_subject);
    }

    @Test
    public void testNewPostValidationEmptyContent() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(newPostUI);
        boardPostListController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
                "Title", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_some_content);
    }


    @Test
    public void testNewPostFails() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);
        when(disBoardRepo.addNewPost(BoardListTypes.SOCIAL, "New title", "New content"))
                .thenReturn(Observable.create(new Observable.OnSubscribe<BoardPost>() {
                    @Override
                    public void call(Subscriber<? super BoardPost> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(newPostUI);
        boardPostListController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
                "New title", "New content");

        verify(newPostUI).showLoadingProgress(true);
        verify(newPostUI).showLoadingProgress(false);
        verify(newPostUI).handleNewPostFailure();
    }

    @Test
    public void testNewPostSucceeds(){
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);
        when(disBoardRepo.addNewPost(BoardListTypes.SOCIAL, "New title", "New content"))
                .thenReturn(Observable.just(expectedBoardPost));

        boardPostListController.attachDisplay(display);
        boardPostListController.attachUi(newPostUI);
        boardPostListController.addNewPost(newPostUI, BoardListTypes.SOCIAL, "New title",
                "New content");

        verify(newPostUI).showLoadingProgress(true);
        verify(display).hideCurrentScreen();
        verify(display).showBoardPost(BoardListTypes.SOCIAL, expectedBoardPost.getBoardPostID());
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

    @Test
    public void testScrollCurrentListToPosition() {
        boardPostListController.attachUi(boardPostListUi);
        boardPostListController.attachUi(boardPostListParentUi);

        when(boardPostListUi.getID()).thenReturn(1);
        when(boardPostListParentUi.getID()).thenReturn(2);


        when(boardPostListParentUi.getCurrentPageShow()).thenReturn(1);
        when(boardPostListUi.getPageIndex()).thenReturn(1);
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        boardPostListController.moveToTopOfCurrentList();

        verify(boardPostListUi).scrollToPostAt(0);
    }


}
