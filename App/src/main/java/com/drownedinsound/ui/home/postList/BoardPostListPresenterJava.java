package com.drownedinsound.ui.home.postList;


import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import com.drownedinsound.BoardPostSummaryModel;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.Navigator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

@AutoFactory
public class BoardPostListPresenterJava implements BoardPostListContract.Presenter {

    private final BoardPostListContract.View boardListView;

    private final DisBoardRepo disBoardRepo;

    private final Scheduler mainThreadScheduler;

    private final Scheduler backgroundThreadScheduler;

    private final Navigator navigator;

    private final BoardPostListModelMapper boardPostListModelMapper;

    private Subscription loadingSubscription;

    private List<BoardPostSummary> summaries = new ArrayList<>();

    public BoardPostListPresenterJava(
            BoardPostListContract.View boardListView,
            Navigator navigator,
            @Provided DisBoardRepo disBoardRepo,
            @Provided @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @Provided @ForIoScheduler Scheduler backgroundThreadScheduler,
            BoardPostListModelMapper boardPostListModelMapper) {
        this.boardListView = boardListView;
        this.navigator = navigator;
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
        this.boardPostListModelMapper = boardPostListModelMapper;
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
                    .map(new Func1<List<BoardPostSummary>, List<BoardPostSummaryModel>>() {
                        @Override
                        public List<BoardPostSummaryModel> call(
                                List<BoardPostSummary> boardPostSummaries) {
                            return storeAndMap(boardPostSummaries);
                        }
                    })
                    .subscribeOn(backgroundThreadScheduler)
                    .observeOn(mainThreadScheduler)
                    .subscribe(new Action1<List<BoardPostSummaryModel>>() {
                        @Override
                        public void call(List<BoardPostSummaryModel> boardPostSummaryModels) {
                            handleItems(boardPostSummaryModels);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            handleError(throwable);
                        }
                    });
        }
    }

    private void handleItems(List<BoardPostSummaryModel> boardPostSummaryModels) {
        boardListView.showBoardPostSummaries(boardPostSummaryModels);
        boardListView.showLoadingProgress(false);
    }

    private void handleError(Throwable throwable) {
        boardListView.showLoadingProgress(false);
        boardListView.showErrorView();
    }

    private List<BoardPostSummaryModel> storeAndMap(List<BoardPostSummary> boardPostSummaries) {
        this.summaries = boardPostSummaries;
        return boardPostListModelMapper.map(boardPostSummaries);
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
    public void handleBoardPostSelected(BoardPostSummaryModel boardPostSummaryModel) {
        BoardPostSummary boardPostSummary = summaries.get(boardPostSummaryModel.getIndex());

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
