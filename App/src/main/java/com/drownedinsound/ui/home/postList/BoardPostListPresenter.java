package com.drownedinsound.ui.home.postList;


import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.Navigator;

import java.util.Date;
import java.util.List;

import rx.Observer;
import rx.Scheduler;
import rx.Subscription;

@AutoFactory
public class BoardPostListPresenter implements BoardPostListContract.Presenter {

    private final BoardPostListContract.View boardListView;

    private final DisBoardRepo disBoardRepo;

    private final Scheduler mainThreadScheduler;

    private final Scheduler backgroundThreadScheduler;

    private final Navigator navigator;

    private Subscription loadingSubscription;

    public BoardPostListPresenter(
            BoardPostListContract.View boardListView,
            Navigator navigator,
            @Provided DisBoardRepo disBoardRepo,
            @Provided @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @Provided @ForIoScheduler Scheduler backgroundThreadScheduler) {
        this.boardListView = boardListView;
        this.navigator = navigator;
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
    }


    @Override
    public void onViewCreated() {
        loadList(false);
        boardListView.setPresenter(this);
    }

    @Override
    public void onViewDisplayed() {

    }

    private void loadList(boolean forceUpdate) {
        if(loadingSubscription == null || loadingSubscription.isUnsubscribed()) {
            boardListView.showLoadingProgress(true);

            loadingSubscription = disBoardRepo
                    .getBoardPostSummaryList(boardListView.getBoardListType(), 0, forceUpdate)
                    .subscribeOn(backgroundThreadScheduler)
                    .observeOn(mainThreadScheduler)
                    .subscribe(new Observer<List<BoardPostSummary>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            boardListView.showLoadingProgress(false);
                            boardListView.showErrorView();
                        }

                        @Override
                        public void onNext(List<BoardPostSummary> boardPostLists) {
                            boardListView.showBoardPostSummaries(boardPostLists);
                            boardListView.showLoadingProgress(false);
                        }
                    });
        }
    }


    @Override
    public void onViewHidden() {

    }

    private void stopLoading() {
        if (loadingSubscription != null) {
            loadingSubscription.unsubscribe();
            loadingSubscription = null;
        }
        boardListView.showLoadingProgress(false);
    }

    @Override
    public void onViewDestroyed() {
        stopLoading();
    }

    @Override
    public void handleRefresh() {
        loadList(true);
    }

    @Override
    public void handleBoardPostSelected(BoardPostSummary boardPostSummary) {
        @BoardPostList.BoardPostListType String boardListType
                = boardPostSummary.getBoardListTypeID();
        boardPostSummary.setLastViewedTime(new Date().getTime());
        boardPostSummary.setNumberOfTimesOpened(boardPostSummary.getNumberOfTimesOpened() + 1);

        disBoardRepo.setBoardPostSummary(boardPostSummary)
                .subscribeOn(backgroundThreadScheduler)
                .subscribe();

        navigator.showBoardPostScreen(boardListType,boardPostSummary.getBoardPostID());
    }


}
