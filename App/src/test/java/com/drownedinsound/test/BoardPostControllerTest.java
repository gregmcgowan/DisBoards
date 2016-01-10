package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.ui.post.BoardPostController;
import com.drownedinsound.ui.post.BoardPostUI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by gregmcgowan on 09/01/16.
 */
public class BoardPostControllerTest {

    @Mock
    BoardPostUI boardPostUI;

    @Mock
    DisBoardRepo disBoardRepo;

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
        when(disBoardRepo.getBoardPost(BoardListTypes.SOCIAL, "12345",true)).thenReturn(
                Observable.just(expectedBoardPost));

        boardPostController.attachUi(boardPostUI);

        boardPostController.loadBoardPost(boardPostUI, BoardListTypes.SOCIAL, "12345", true);

        verify(boardPostUI).showLoadingProgress(true);
        verify(boardPostUI).showBoardPost(expectedBoardPost, -1);
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
}
