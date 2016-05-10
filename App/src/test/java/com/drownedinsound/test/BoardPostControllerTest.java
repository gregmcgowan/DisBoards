package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.ui.post.BoardPostController;
import com.drownedinsound.ui.post.BoardPostUI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 09/01/16.
 */
public class BoardPostControllerTest {

    @Mock
    BoardPostUI boardPostUI;

    @Mock
    DisBoardRepo disBoardRepo;

    @Mock
    Display display;

    BoardPostController boardPostController;

    BoardPost expectedBoardPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        boardPostController = new BoardPostController(disBoardRepo, Schedulers.immediate(),Schedulers.immediate());

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
    public void testGetPostSuccessfully() {
        BoardPostSummary boardPostSummary = new BoardPostSummary();
        boardPostSummary.setBoardPostID("12345");

        when(disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, "12345",true)).thenReturn(
                Observable.just(expectedBoardPost));
        when(disBoardRepo.getBoardPostSummary(BoardListTypes.SOCIAL,"12345"))
                .thenReturn(Observable.just(boardPostSummary));

        boardPostController.attachUi(boardPostUI);

        boardPostController.loadBoardPost(boardPostUI, BoardListTypes.SOCIAL, "12345", true);

        verify(boardPostUI).showLoadingProgress(true);
        verify(boardPostUI).showBoardPost(expectedBoardPost);
        verify(boardPostUI).showLoadingProgress(false);

        boardPostController.detachUi(boardPostUI);
    }

    @Test
    public void testBoardPostGoToLastCommentOptionShown() {
        BoardPostSummary boardPostSummary = new BoardPostSummary();
        boardPostSummary.setBoardPostID("12345");
        boardPostSummary.setNumberOfTimesOpened(2);

        expectedBoardPost.setNumberOfReplies(2);

        when(disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, "12345", true)).thenReturn(
                Observable.just(expectedBoardPost));
        when(disBoardRepo.getBoardPostSummary(BoardListTypes.SOCIAL, "12345"))
                .thenReturn(Observable.just(boardPostSummary));
        when(boardPostUI.userHasInteractedWithUI()).thenReturn(false);

        boardPostController.attachUi(boardPostUI);

        boardPostController.loadBoardPost(boardPostUI, BoardListTypes.SOCIAL, "12345", true);

        verify(boardPostUI).showLoadingProgress(true);
        verify(boardPostUI).showBoardPost(expectedBoardPost);

        ArgumentCaptor<DisBoardsLoadingLayout.ContentShownListener>
                captor = ArgumentCaptor.forClass(DisBoardsLoadingLayout.ContentShownListener.class);
        verify(boardPostUI).setOnContentShownListener(captor.capture());
        verify(boardPostUI).showLoadingProgress(false);

        boardPostController.detachUi(boardPostUI);
    }

    @Test
    public void testBoardPostGoToLastCommentOptionNotShown() {
        BoardPostSummary boardPostSummary = new BoardPostSummary();
        boardPostSummary.setBoardPostID("12345");
        boardPostSummary.setNumberOfTimesOpened(1);

        expectedBoardPost.setNumberOfReplies(2);

        when(disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, "12345", true)).thenReturn(
                Observable.just(expectedBoardPost));
        when(disBoardRepo.getBoardPostSummary(BoardListTypes.SOCIAL, "12345"))
                .thenReturn(Observable.just(boardPostSummary));
        when(boardPostUI.userHasInteractedWithUI()).thenReturn(false);

        boardPostController.attachUi(boardPostUI);

        boardPostController.loadBoardPost(boardPostUI, BoardListTypes.SOCIAL, "12345", true);

        verify(boardPostUI).showLoadingProgress(true);
        verify(boardPostUI).showBoardPost(expectedBoardPost);

        ArgumentCaptor<DisBoardsLoadingLayout.ContentShownListener>
                captor = ArgumentCaptor.forClass(DisBoardsLoadingLayout.ContentShownListener.class);
        verify(boardPostUI, never()).setOnContentShownListener(captor.capture());
        verify(boardPostUI).showLoadingProgress(false);

        boardPostController.detachUi(boardPostUI);
    }


    @Test
    public void testGetBoardPostError() {
        when(disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, "12345", true)).thenReturn(
                Observable.create(new Observable.OnSubscribe<BoardPost>() {
                    @Override
                    public void call(Subscriber<? super BoardPost> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        boardPostController.attachUi(boardPostUI);

        boardPostController.loadBoardPost(boardPostUI, BoardListTypes.SOCIAL, "12345", true);

        verify(boardPostUI).showLoadingProgress(true);
        verify(boardPostUI).showErrorView();
    }

    @Test
    public void testShowReplyUiLoggedIn() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        boardPostController.attachDisplay(display);
        boardPostController.showReplyUI(BoardListTypes.SOCIAL, "Author", "12345", "1224");

        verify(display).showReplyUI(BoardListTypes.SOCIAL, "Author", "12345", "1224");
    }

    @Test
    public void testShowReplyUiNotLoggedIn() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(false);

        boardPostController.attachDisplay(display);
        boardPostController.showReplyUI(BoardListTypes.SOCIAL, "Author", "12345", "1224");

        verify(display).showNotLoggedInUI();
    }

    @Test
    public void testThisACommentNotLoggedIn() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(false);

        boardPostController.attachDisplay(display);
        boardPostController.thisAComment(boardPostUI, BoardListTypes.SOCIAL, "Author", "12345");

        verify(display).showNotLoggedInUI();
    }

    @Test
    public void testThisACommentFails() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);
        when(disBoardRepo.thisAComment(BoardListTypes.SOCIAL, "postID", "commentID"))
                .thenReturn(Observable.create(new Observable.OnSubscribe<BoardPost>() {
                    @Override
                    public void call(Subscriber<? super BoardPost> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));


        boardPostController.attachDisplay(display);
        boardPostController.attachUi(boardPostUI);
        boardPostController.thisAComment(boardPostUI, BoardListTypes.SOCIAL, "postID", "commentID");


        verify(boardPostUI).showLoadingProgress(true);
        verify(boardPostUI).showLoadingProgress(false);
        verify(boardPostUI).showThisACommentFailed();
    }

    @Test
    public void testThisACommentSucceeds(){
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);
        when(disBoardRepo.thisAComment(BoardListTypes.SOCIAL, "Author", "12345"))
                .thenReturn(Observable.just(expectedBoardPost));

        boardPostController.attachDisplay(display);
        boardPostController.attachUi(boardPostUI);
        boardPostController.thisAComment(boardPostUI, BoardListTypes.SOCIAL, "Author", "12345");

        verify(boardPostUI).showLoadingProgress(true);
        verify(boardPostUI).showLoadingProgress(false);
    }

}
