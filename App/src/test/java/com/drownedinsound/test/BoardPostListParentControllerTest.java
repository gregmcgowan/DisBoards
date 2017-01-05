package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.ui.postList.BoardPostListParentController;
import com.drownedinsound.ui.postList.BoardPostListParentUi;
import com.drownedinsound.ui.postList.BoardPostListUi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 10/05/2016.
 */
public class BoardPostListParentControllerTest {

    @Mock
    BoardPostListUi boardPostListUi;

    @Mock
    BoardPostListParentUi boardPostListParentUi;

    @Mock
    DisBoardRepo disBoardRepo;

    @Mock
    Display display;

    private BoardPostListParentController boardPostListParentController;

    private BoardPostList boardPostListInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        boardPostListParentController = new BoardPostListParentController(disBoardRepo, Schedulers.immediate(),
                Schedulers.immediate());

        boardPostListInfo = new BoardPostList(BoardListTypes.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 0, 19,0);

    }

    @Test
    public void testScrollCurrentListToPosition() {
        boardPostListParentController.attachUi(boardPostListUi);
        boardPostListParentController.attachUi(boardPostListParentUi);

        when(boardPostListUi.getID()).thenReturn(1);
        when(boardPostListParentUi.getID()).thenReturn(2);


        when(boardPostListParentUi.getCurrentPageShow()).thenReturn(1);
        when(boardPostListUi.getPageIndex()).thenReturn(1);
        when(boardPostListUi.getBoardListType()).thenReturn(BoardListTypes.MUSIC);

        boardPostListParentController.moveToTopOfCurrentList(boardPostListUi);

        verify(boardPostListUi).scrollToPostAt(0);
    }

    @Test
    public void testGetListInfo() {
        List<BoardPostList> boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(boardPostListInfo);

        when(disBoardRepo.getAllBoardPostLists())
                .thenReturn(Observable.just(boardPostListInfos));

        when(boardPostListParentUi.getID()).thenReturn(1);

        boardPostListParentController.uiCreated(boardPostListParentUi);
        boardPostListParentController.attachUi(boardPostListParentUi);
        boardPostListParentController.detachUi(boardPostListParentUi);
        boardPostListParentController.attachUi(boardPostListParentUi);

        verify(disBoardRepo).getAllBoardPostLists();
        verify(boardPostListParentUi,times(1)).setBoardPostLists(boardPostListInfos);

    }



}
