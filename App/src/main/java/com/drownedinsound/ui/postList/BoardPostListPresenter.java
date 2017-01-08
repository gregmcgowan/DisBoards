package com.drownedinsound.ui.postList;


import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;

import rx.Scheduler;

@AutoFactory
public class BoardPostListPresenter implements BoardPostListContract.Presenter {

    private final BoardPostListContract.View boardListView;

    private final DisBoardRepo disBoardRepo;

    private final Scheduler mainThreadScheduler;

    private final Scheduler backgroundThreadScheduler;

    public BoardPostListPresenter(BoardPostListContract.View boardListView,
            @Provided DisBoardRepo disBoardRepo,
            @Provided @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @Provided @ForIoScheduler Scheduler backgroundThreadScheduler) {
        this.boardListView = boardListView;
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
    }


    @Override
    public void onViewCreated() {

    }

    @Override
    public void onViewDisplayed() {
        getBoardPostSummaryList(boardListView.getBoardListType());
    }

    private void getBoardPostSummaryList(String boardListType) {
    }

    @Override
    public void onViewHidden() {
        boardListView.showLoadingProgress(false);
    }

    @Override
    public void onViewDestroyed() {

    }
}
