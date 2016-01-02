package com.drownedinsound.ui.postList;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 22/03/15.
 */
@Singleton
public class BoardPostListController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    private Scheduler mainThreadScheduler;

    private Scheduler backgroundThreadScheduler;

    @Inject
    public BoardPostListController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
    }


    @Override
    public void onUiAttached(Ui ui) {
        if (ui instanceof BoardPostListUi) {
            BoardPostListUi boardPostListUi = (BoardPostListUi) ui;
            if (boardPostCurrentShow(boardPostListUi)) {
                requestBoardSummaryPage(boardPostListUi,
                        boardPostListUi.getBoardListType(), 1, false);
            }
        }

        if(ui instanceof  BoardPostListParentUi) {
            BoardPostListParentUi boardPostListParentUi
                    = (BoardPostListParentUi) ui;
            if(boardPostListParentUi.getNoOfBoardListShown() == 0) {
                int id = getId(boardPostListParentUi);
                Observable<List<BoardPostList>>
                    getBoardPostListInfoObservable = disBoardRepo.getAllBoardPostLists()
                        .subscribeOn(backgroundThreadScheduler)
                        .observeOn(mainThreadScheduler);

                BaseObserver<List<BoardPostList>,BoardPostListParentUi>
                        getBoardPostListInfoObserever = new BaseObserver<List<BoardPostList>, BoardPostListParentUi>(id) {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BoardPostList> boardPostListInfos) {
                        getUI().setBoardPostLists(boardPostListInfos);
                    }
                };
                subscribeAndCache(boardPostListParentUi,"INFO",getBoardPostListInfoObserever,getBoardPostListInfoObservable);
            }
        }
    }

    private boolean boardPostCurrentShow(BoardPostListUi boardPostListUi) {
        BoardPostListParentUi boardPostListParentUi = findUi(BoardPostListParentUi.class);
        if (boardPostListParentUi != null) {
            return boardPostListParentUi.boardPostListShown(boardPostListUi);
        }
        return false;
    }

    public void loadListAt(int position) {
        BoardPostListUi boardPostList = findListAt(position);
        if (boardPostList != null) {
            requestBoardSummaryPage(boardPostList,
                    boardPostList.getBoardListType(), 1, false);
        }
    }

    private BoardPostListUi findListAt(int position) {
        Set<Ui> uis = getUis();
        for (Ui ui : uis) {
            if (ui instanceof BoardPostListUi) {
                BoardPostListUi boardPostListUi
                        = (BoardPostListUi) ui;

                if (boardPostListUi.getPageIndex()
                        == position) {
                    return boardPostListUi;
                }
            }
        }
        return null;
    }

    public void requestBoardSummaryPage(@NonNull BoardPostListUi boardPostListUi,
            @NonNull @BoardPostList.BoardPostListType String boardListType, final int page, boolean forceUpdate) {
        if (!hasSubscription(boardPostListUi, boardListType)) {
            if (page == 1) {
                boardPostListUi.showLoadingProgress(true);
            }
            int uiId = getId(boardPostListUi);
            Timber.d("Going to update id " + uiId
                    + " for board " + boardListType);
            Observable<List<BoardPost>> getBoardPostListObservable = disBoardRepo
                    .getBoardPostList(boardListType, page, forceUpdate)
                    .subscribeOn(backgroundThreadScheduler)
                    .observeOn(mainThreadScheduler);

            BaseObserver<List<BoardPost>, BoardPostListUi> getboardPostListObserver =
                    new BaseObserver<List<BoardPost>, BoardPostListUi>(uiId) {
                        @Override
                        public void onError(Throwable e) {
                            getUI().showLoadingProgress(false);
                            getUI().showErrorView();
                        }

                        @Override
                        public void onNext(List<BoardPost> boardPostList) {
                            if (page == 1) {
                                getUI().setBoardPosts(boardPostList);
                            } else {
                                getUI().appendBoardPosts(boardPostList);
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


}