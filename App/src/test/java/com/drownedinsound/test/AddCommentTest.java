package com.drownedinsound.test;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.ui.addComment.AddCommentController;
import com.drownedinsound.ui.addComment.AddCommentUi;
import com.drownedinsound.ui.base.Display;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 10/05/2016.
 */
public class AddCommentTest {

    @Mock
    DisBoardRepo disBoardRepo;

    @Mock
    Display display;

    @Mock
    AddCommentUi addCommentUi;

    private AddCommentController addCommentController;

    private BoardPost expectedBoardPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        addCommentController = new AddCommentController(disBoardRepo, Schedulers.immediate(),
                Schedulers.immediate());

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
    public void testEmptyTitleAndContentReply() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        addCommentController.attachDisplay(display);
        addCommentController.attachUi(addCommentUi);
        addCommentController.replyToComment(addCommentUi, BoardListTypes.SOCIAL, "12345", null,
                "", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_some_content);
    }

    @Test
    public void testEmptyTitleReply() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);
        when(disBoardRepo.postComment(BoardListTypes.SOCIAL, "12345", "12345", "", "Content"))
                .thenReturn(Observable.just(expectedBoardPost));

        addCommentController.attachDisplay(display);
        addCommentController.attachUi(addCommentUi);
        addCommentController.replyToComment(addCommentUi, BoardListTypes.SOCIAL, "12345", "12345",
                "", "Content");

        verify(addCommentUi).showLoadingProgress(true);
        verify(display).hideCurrentScreen();
    }

    @Test
    public void testEmptyContentReply() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);
        when(disBoardRepo.postComment(BoardListTypes.SOCIAL, "12345", "12345", "Title", ""))
                .thenReturn(Observable.just(expectedBoardPost));

        addCommentController.attachDisplay(display);
        addCommentController.attachUi(addCommentUi);
        addCommentController.replyToComment(addCommentUi, BoardListTypes.SOCIAL, "12345", "12345",
                "Title", "");

        verify(addCommentUi).showLoadingProgress(true);
        verify(display).hideCurrentScreen();
    }

    @Test
    public void testReplyToCommentSucceeds() {
        when(disBoardRepo.postComment(BoardListTypes.SOCIAL,"12345","12345", "Title", "Content"))
                .thenReturn(Observable.just(expectedBoardPost));

        addCommentController.attachDisplay(display);
        addCommentController.attachUi(addCommentUi);
        addCommentController.replyToComment(addCommentUi,
                BoardListTypes.SOCIAL, "12345", "12345", "Title", "Content");

        verify(addCommentUi).showLoadingProgress(true);
        verify(display).hideCurrentScreen();
    }


    @Test
    public void testReplyToCommentFails() {
        when(disBoardRepo.postComment(BoardListTypes.SOCIAL,"12345","12345", "Title", "Content"))
                .thenReturn(Observable.create(new Observable.OnSubscribe<BoardPost>() {
                    @Override
                    public void call(Subscriber<? super BoardPost> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        addCommentController.attachDisplay(display);
        addCommentController.attachUi(addCommentUi);
        addCommentController.replyToComment(addCommentUi,
                BoardListTypes.SOCIAL, "12345", "12345", "Title", "Content");

        verify(addCommentUi).showLoadingProgress(true);
        verify(addCommentUi).showLoadingProgress(false);
        verify(addCommentUi).handlePostCommentFailure();
    }
}
