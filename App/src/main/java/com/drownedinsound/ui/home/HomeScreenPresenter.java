package com.drownedinsound.ui.home;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.home.postList.BoardPostListContract;

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

    private final Map<String, BoardPostListContract.Presenter> boardPostListPresenterMap
            = new HashMap<>();

    public HomeScreenPresenter(
            HomeScreenContract.View homeScreenView,
            DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        this.homeScreenView = homeScreenView;
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
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
    public void addBoardListPresenter(String type, BoardPostListContract.Presenter presenter) {
        boardPostListPresenterMap.put(type, presenter);
        presenter.onViewCreated();
    }

    @Override
    public void removeBoardPostListView(String type) {
        BoardPostListContract.Presenter removedListPresenter = boardPostListPresenterMap
                .remove(type);
        removedListPresenter.onViewDestroyed();
    }

    @Override
    public void handleListDisplayed(String type) {
        BoardPostListContract.Presenter boardPostListPresenter = boardPostListPresenterMap
                .get(type);
        boardPostListPresenter.onViewDisplayed();
    }

    @Override
    public void handlePageTabReselected(int position) {

    }
}
