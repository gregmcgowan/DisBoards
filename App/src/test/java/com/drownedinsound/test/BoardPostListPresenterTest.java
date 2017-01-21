package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.ui.base.Navigator;
import com.drownedinsound.ui.home.postList.BoardPostListContract;
import com.drownedinsound.ui.home.postList.BoardPostListPresenter;
import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardPostListPresenterTest {

    @Mock
    BoardPostListContract.View mockBoardPostList;

    @Mock
    DisBoardRepo disBoardRepo;

    @Mock
    Navigator display;

    @Fixture
    List<BoardPostSummary> boardPosts;

    private BoardPostListPresenter boardPostListPresenter;

    private Scheduler testScheduler = Schedulers.immediate();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FixtureAnnotations.initFixtures(this);

        boardPostListPresenter = new BoardPostListPresenter(mockBoardPostList, display,
                disBoardRepo,
                testScheduler, testScheduler);

        when(mockBoardPostList.getBoardListType()).thenReturn(BoardListTypes.MUSIC);
    }

    @Test
    public void getSummariesSuccess() {
        when(disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, 0, false))
                .thenReturn(Observable.just(boardPosts));

        boardPostListPresenter.onViewCreated();

        verify(mockBoardPostList).showLoadingProgress(true);
        verify(mockBoardPostList).showBoardPostSummaries(boardPosts);
        verify(mockBoardPostList).showLoadingProgress(false);
    }

    @Test
    public void getSummariesError() {
        when(disBoardRepo.getBoardPostSummaryList(BoardListTypes.MUSIC, 0, false))
                .thenReturn(Observable.<List<BoardPostSummary>>error(new Exception()));

        boardPostListPresenter.onViewCreated();

        verify(mockBoardPostList).showLoadingProgress(true);
        verify(mockBoardPostList).showErrorView();
        verify(mockBoardPostList).showLoadingProgress(false);
    }

    @Test
    public void onViewDestroyed() {
        boardPostListPresenter.onViewDestroyed();

        verify(mockBoardPostList).showLoadingProgress(false);
    }

    @Test
    public void boardPostSelected() {
        BoardPostSummary boardPostSummary = boardPosts.get(0);

        when(disBoardRepo.setBoardPostSummary(any(BoardPostSummary.class)))
                .thenReturn(Completable.complete());

        boardPostListPresenter.handleBoardPostSelected(boardPostSummary);

        verify(display).showBoardPostScreen(boardPostSummary.getBoardListTypeID(),
                boardPostSummary.getBoardPostID());
    }


}
