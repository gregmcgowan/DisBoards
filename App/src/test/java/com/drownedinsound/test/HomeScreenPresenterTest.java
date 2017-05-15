package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.ui.home.HomeScreenContract;
import com.drownedinsound.ui.home.postList.BoardPostListContract;
import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class HomeScreenPresenterTest {

    @Mock
    HomeScreenContract.View mockHomeScreenView;

    @Mock
    DisBoardRepo mockDisBoardRepo;

    @Mock
    BoardPostListContract.Presenter mockBoardPostListPresenter;

    @Fixture
    List<BoardPostList> boardPostLists;

    private Scheduler testScheduler = Schedulers.immediate();

    private HomeScreenPresenter homeScreenPresenter;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        FixtureAnnotations.initFixtures(this);

        homeScreenPresenter = new HomeScreenPresenter(mockHomeScreenView,
                mockDisBoardRepo, testScheduler,testScheduler);

        when(mockDisBoardRepo.getAllBoardPostLists())
                .thenReturn(Observable.just(boardPostLists));
    }

    @Test
    public void onViewCreated() throws Exception {
        homeScreenPresenter.onViewCreated();
        verify(mockHomeScreenView).showBoardPostLists(boardPostLists);
    }

    @Test
    public void addBoardPostListPresenter() throws Exception {
        homeScreenPresenter.addBoardListPresenter(BoardListTypes.MUSIC,
                mockBoardPostListPresenter);
        verify(mockBoardPostListPresenter).onViewCreated();
    }

    @Test
    public void removeBoardPostListPresenter() throws Exception {
        homeScreenPresenter.addBoardListPresenter(BoardListTypes.MUSIC,
                mockBoardPostListPresenter);

        homeScreenPresenter.removeBoardPostListView(BoardListTypes.MUSIC);
        verify(mockBoardPostListPresenter).onViewDestroyed();
    }

    @Test
    public void handleListDisplayed() throws Exception {
        homeScreenPresenter.addBoardListPresenter(BoardListTypes.MUSIC,
                mockBoardPostListPresenter);

        homeScreenPresenter.handleListDisplayed(BoardListTypes.MUSIC);

    }

    @Test
    public void handlePageTabReselected() throws Exception {

    }

}