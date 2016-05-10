package com.drownedinsound.test;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.ui.addPost.AddPostController;
import com.drownedinsound.ui.addPost.AddPostUI;
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
public class AddPostControllerTest {

    @Mock
    DisBoardRepo disBoardRepo;

    @Mock
    Display display;

    @Mock
    AddPostUI newPostUI;

    private AddPostController addPostController;

    private BoardPost expectedBoardPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        addPostController = new AddPostController(disBoardRepo, Schedulers.immediate(),
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
    public void testNewPostValidationEmptyStrings() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        addPostController.attachDisplay(display);
        addPostController.attachUi(newPostUI);
        addPostController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
                "", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_some_content_and_subject);
    }

    @Test
    public void testNewPostValidationEmptyTitle() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        addPostController.attachDisplay(display);
        addPostController.attachUi(newPostUI);
        addPostController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
                "", "Some content");

        verify(display).showErrorMessageDialog(R.string.please_enter_a_subject);
    }

    @Test
    public void testNewPostValidationEmptyContent() {
        when(disBoardRepo.isUserLoggedIn()).thenReturn(true);

        addPostController.attachDisplay(display);
        addPostController.attachUi(newPostUI);
        addPostController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
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

        addPostController.attachDisplay(display);
        addPostController.attachUi(newPostUI);
        addPostController.addNewPost(newPostUI, BoardListTypes.SOCIAL,
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

        addPostController.attachDisplay(display);
        addPostController.attachUi(newPostUI);
        addPostController.addNewPost(newPostUI, BoardListTypes.SOCIAL, "New title",
                "New content");

        verify(newPostUI).showLoadingProgress(true);
        verify(display).hideCurrentScreen();
        verify(display).showBoardPost(BoardListTypes.SOCIAL, expectedBoardPost.getBoardPostID());
    }
}
