package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.ui.base.Navigator;
import com.drownedinsound.ui.post.BoardPostContract;
import com.drownedinsound.ui.post.BoardPostPresenter;
import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardPostPresenterTest {

    @Mock
    BoardPostContract.View mockBoardPostView;

    @Mock
    DisBoardRepo disBoardRepo;

    @Mock
    Navigator navigator;

    @Mock
    BoardPost expectedBoardPost;

    @Fixture
    BoardPostSummary boardPostSummary;

    @Fixture
    String boardPostId;

    @Fixture
    String boardType;

    Scheduler testScheduler = Schedulers.immediate();

    BoardPostPresenter boardPostPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FixtureAnnotations.initFixtures(this);

        boardPostPresenter = new BoardPostPresenter(boardPostId,
                boardType, mockBoardPostView, navigator, testScheduler,
                testScheduler, disBoardRepo);
    }

    @Test
    public void getPostSuccessfully() {
        when(disBoardRepo.getBoardPost(boardType, boardPostId, false)).thenReturn(
                Observable.just(expectedBoardPost));

        when(disBoardRepo.getBoardPostSummary(boardType, boardPostId))
                .thenReturn(Observable.just(boardPostSummary));

        boardPostPresenter.onViewCreated();

        verify(mockBoardPostView).showLoadingProgress(true);
        verify(mockBoardPostView).showBoardPost(expectedBoardPost);
        verify(mockBoardPostView).showLoadingProgress(false);
    }


    @Test
    public void getPostError() {
        when(disBoardRepo.getBoardPost(boardType, boardPostId, false))
                .thenReturn(Observable.<BoardPost>error(new Exception()));

        boardPostPresenter.onViewCreated();

        verify(mockBoardPostView).showLoadingProgress(true);
        verify(mockBoardPostView).showErrorView();
    }

    @Test
    public void refresh() {
        when(disBoardRepo.getBoardPost(boardType, boardPostId, true)).thenReturn(
                Observable.just(expectedBoardPost));

        when(disBoardRepo.getBoardPostSummary(boardType, boardPostId))
                .thenReturn(Observable.just(boardPostSummary));

        boardPostPresenter.handleRefreshAction();

        verify(mockBoardPostView).showLoadingProgress(true);
        verify(mockBoardPostView).showBoardPost(expectedBoardPost);
        verify(mockBoardPostView).showLoadingProgress(false);
    }

    @Test
    public void handleBack() {
        boardPostPresenter.handleBackAction();

        verify(navigator).hideCurrentScreen();
    }

}
