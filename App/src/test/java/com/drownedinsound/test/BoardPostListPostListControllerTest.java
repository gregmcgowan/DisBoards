package com.drownedinsound.test;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.model.BoardListTypes;
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

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
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
    FakeDisRepo fakeDisRepo;

    private BoardPostListController boardPostController;

    private BoardPostList boardPostListInfo;

    private @BoardPostList.BoardPostListType String boardListType;

    private List<BoardPost> boardPosts;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        boardPostController = new BoardPostListController(fakeDisRepo, Schedulers.immediate(),
                Schedulers.immediate());
        boardListType = BoardListTypes.MUSIC;

        boardPostListInfo = new BoardPostList(BoardListTypes.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 0, 19,0);

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
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(fakeDisRepo.getBoardPostList(eq(BoardListTypes.MUSIC), anyInt(), anyBoolean()))
                .thenReturn(Observable.just(boardPosts));

        boardPostController.attachUi(boardPostListUi);

        boardPostController.requestBoardSummaryPage(boardPostListUi, boardListType, 1, true);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).setBoardPosts(boardPosts);
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostController.detachUi(boardPostListUi);
    }


    @Test
    public void testGetListSecondPageSuccessfully() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(fakeDisRepo.getBoardPostList(eq(BoardListTypes.MUSIC), anyInt(), anyBoolean()))
                .thenReturn(Observable.just(boardPosts));

        boardPostController.attachUi(boardPostListUi);

        boardPostController.requestBoardSummaryPage(boardPostListUi, boardListType, 2, true);

        verify(boardPostListUi).appendBoardPosts(boardPosts);

        boardPostController.detachUi(boardPostListUi);
    }

    @Test
    public void testGetListFirstPageError() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(fakeDisRepo.getBoardPostList(eq(BoardListTypes.MUSIC), anyInt(), anyBoolean()))
                .thenReturn(Observable.create(new Observable.OnSubscribe<List<BoardPost>>() {
            @Override
            public void call(Subscriber<? super List<BoardPost>> subscriber) {
                subscriber.onError(new Exception());
            }
        }));

        boardPostController.attachUi(boardPostListUi);

        boardPostController.requestBoardSummaryPage(boardPostListUi, boardListType, 1, true);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).showErrorView();
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostController.detachUi(boardPostListUi);
    }

    @Test
    public void testStopLoadingOnDetach() {
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        boardPostController.attachUi(boardPostListUi);
        boardPostController.detachUi(boardPostListUi);

        verify(boardPostListUi).showLoadingProgress(false);
    }

    @Test
    public void testLoadListOnAttachToParent() {
        when(boardPostListParentUi.boardPostListShown(boardPostListUi)).thenReturn(true);
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        when(boardPostListUi.getId()).thenReturn(1);
        when(boardPostListParentUi.getId()).thenReturn(2);

        when(fakeDisRepo.getBoardPostList(eq(BoardListTypes.MUSIC), anyInt(), anyBoolean()))
                .thenReturn(Observable.just(boardPosts));

        List<BoardPostList> boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(boardPostListInfo);

        when(fakeDisRepo.getAllBoardPostLists())
                .thenReturn(Observable.just(boardPostListInfos));

        boardPostController.attachUi(boardPostListParentUi);
        boardPostController.attachUi(boardPostListUi);

        verify(boardPostListUi).showLoadingProgress(true);
        verify(boardPostListUi).setBoardPosts(boardPosts);
        verify(boardPostListUi).showLoadingProgress(false);

        boardPostController.detachUi(boardPostListUi);
        boardPostController.detachUi(boardPostListParentUi);
    }

    @Test
    public void testGetListInfo() {
        List<BoardPostList> boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(boardPostListInfo);

        when(fakeDisRepo.getAllBoardPostLists())
                .thenReturn(Observable.just(boardPostListInfos));

        when(boardPostListParentUi.getNoOfBoardListShown()).thenReturn(0);

        boardPostController.attachUi(boardPostListParentUi);

        verify(fakeDisRepo).getAllBoardPostLists();
        verify(boardPostListParentUi).setBoardPostLists(boardPostListInfos);

        boardPostController.detachUi(boardPostListParentUi);
        boardPostController.attachUi(boardPostListParentUi);

        when(boardPostListParentUi.getNoOfBoardListShown()).thenReturn(1);

    }


}
