package com.drownedinsound.ui.postList;

import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 22/03/15.
 */
@SingleIn(BoardPostListFragmentComponent.class)
public class BoardPostListController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    @Inject
    public BoardPostListController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler, backgroundThreadScheduler);
        this.disBoardRepo = disBoardRepo;
    }

    @Override
    public void onUiAttached(Ui ui) {
        if (ui instanceof BoardPostListUi) {
            BoardPostListUi boardPostListUi = (BoardPostListUi) ui;
            if (boardPostListUi.isDisplayed()) {
                requestBoardSummaryPage(boardPostListUi,
                        boardPostListUi.getBoardListType(), 1, false);
            } else {
                Timber.d(boardPostListUi.getBoardListType() + " is not currently shown so not "
                        + "upadting");
            }
        }
    }

    public void requestBoardSummaryPage(@NonNull BoardPostListUi boardPostListUi,
            @NonNull @BoardPostList.BoardPostListType String boardListType, final int page,
            boolean forceUpdate) {
        if (!hasSubscription(boardPostListUi, boardListType)) {
            if (page == 1) {
                boardPostListUi.showLoadingProgress(true);
            }
            int uiId = getId(boardPostListUi);
            Timber.d("Going to update id " + uiId
                    + " for board " + boardListType);
            Observable<List<BoardPostSummary>> getBoardPostListObservable = disBoardRepo
                    .getBoardPostSummaryList(boardListType, page, forceUpdate)
                    .compose(this.<List<BoardPostSummary>>defaultTransformer());

            BaseObserver<List<BoardPostSummary>, BoardPostListUi> getboardPostListObserver =
                    new BaseObserver<List<BoardPostSummary>, BoardPostListUi>(uiId) {
                        @Override
                        public void onError(Throwable e) {
                            getUI().showLoadingProgress(false);
                            getUI().showErrorView();
                        }

                        @Override
                        public void onNext(List<BoardPostSummary> boardPostList) {

                            if (page == 1) {
                                getUI().setBoardPostSummaries(boardPostList);
                            } else {
                                getUI().appendBoardPostSummaries(boardPostList);
                            }
                            getUI().showLoadingProgress(false);
                        }
                    };
            subscribeAndCache(boardPostListUi, boardListType, getboardPostListObserver,
                    getBoardPostListObservable);
        } else {
            Timber.d("Request for " + boardListType + " already in progress");
        }
    }

    @Override
    public void onUiDetached(Ui ui) {
        if (ui instanceof BoardPostListUi) {
            BoardPostListUi boardPostListUi = ((BoardPostListUi) ui);
            boardPostListUi.showLoadingProgress(false);
        }
    }


    public void handleBoardPostSummarySelected(BoardPostListUi boardPostListUi,
            BoardPostSummary boardPostSummary) {
        @BoardPostList.BoardPostListType String boardListType
                = boardPostSummary.getBoardListTypeID();
        boardPostSummary.setLastViewedTime(new Date().getTime());
        boardPostSummary.setNumberOfTimesOpened(boardPostSummary.getNumberOfTimesOpened() + 1);

        Observable<Void> observable = disBoardRepo.setBoardPostSummary(boardPostSummary)
                .subscribeOn(getBackgroundThreadScheduler());

        BaseObserver<Void, BoardPostListUi> updateboardPostSummary
                = new BaseObserver<Void, BoardPostListUi>(getId(boardPostListUi)) {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Void aVoid) {

            }
        };

        subscribeAndCache(boardPostListUi, "UPDATE_SUMMARY", updateboardPostSummary, observable);

        getDisplay().showBoardPost(boardListType, boardPostSummary.getBoardPostID());
    }
}
