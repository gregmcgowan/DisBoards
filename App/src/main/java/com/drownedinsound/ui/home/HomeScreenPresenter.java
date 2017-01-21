package com.drownedinsound.ui.home;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.Navigator;
import com.drownedinsound.ui.home.postList.BoardPostListContract;
import com.drownedinsound.ui.home.postList.BoardPostListPresenter;
import com.drownedinsound.ui.home.postList.BoardPostListPresenterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Scheduler;

public class HomeScreenPresenter implements HomeScreenContract.Presenter {

    private final HomeScreenContract.View homeScreenView;

    private final DisBoardRepo disBoardRepo;

    private final Scheduler mainThreadScheduler;
    private final Scheduler backgroundThreadScheduler;

    private final BoardPostListPresenterFactory boardPostListPresenterFactory;
    private final Map<String,BoardPostListPresenter> boardPostListPresenterMap = new HashMap<>();

    public HomeScreenPresenter(
            HomeScreenContract.View homeScreenView,
            DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler,
            BoardPostListPresenterFactory boardPostListPresenterFactory) {
        this.homeScreenView = homeScreenView;
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
        this.boardPostListPresenterFactory = boardPostListPresenterFactory;
    }

    @Override
    public void onViewCreated() {
        getAllBoardPostLists();
    }

    private void getAllBoardPostLists() {
        disBoardRepo.getAllBoardPostLists()
                .subscribeOn(backgroundThreadScheduler)
                .observeOn(mainThreadScheduler)
                .subscribe(new Observer<List<BoardPostList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BoardPostList> boardPostLists) {
                        homeScreenView.showBoardPostLists(boardPostLists);
                    }
                });
    }

    @Override
    public void addBoardPostListView(BoardPostListContract.View view, Navigator navigator,
            String type) {
        BoardPostListPresenter boardPostListPresenter = boardPostListPresenterFactory
                .create(view, navigator);
        boardPostListPresenterMap.put(type,boardPostListPresenter);
        boardPostListPresenter.onViewCreated();
    }

    @Override
    public void removeBoardPostListView(String type) {
        BoardPostListPresenter removedListPresenter = boardPostListPresenterMap.remove(type);
        removedListPresenter.onViewDestroyed();
    }

    @Override
    public void handleListDisplayed(String type) {
        BoardPostListPresenter boardPostListPresenter = boardPostListPresenterMap.get(type);
        boardPostListPresenter.onViewDisplayed();
    }

    @Override
    public void handlePageTabReselected(int position) {

    }
}
