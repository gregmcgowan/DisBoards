package com.drownedinsound.ui.postList;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;

import java.util.List;

import rx.Observer;
import rx.Scheduler;

public class HomeScreenPresenter implements HomeScreenContract.Presenter {

    private final HomeScreenContract.View homeScreenView;
    private final DisBoardRepo disBoardRepo;
    private final HomeScreenAdapterContract.Presenter homeScreenAdapterPresenter;
    private final Scheduler mainThreadScheduler;
    private final Scheduler backgroundThreadScheduler;

    public HomeScreenPresenter(
            HomeScreenContract.View homeScreenView,
            HomeScreenAdapterContract.Presenter homeScreenAdapterPresenter,
            DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        this.homeScreenView = homeScreenView;
        this.disBoardRepo = disBoardRepo;
        this.homeScreenAdapterPresenter = homeScreenAdapterPresenter;
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
    public void onViewDisplayed() {

    }

    @Override
    public void onViewHidden() {

    }

    @Override
    public void onViewDestroyed() {

    }

    @Override
    public void handleListDisplayed(int pageIndex) {

    }

    @Override
    public void handlePageTabReselected(int position) {

    }
}
