package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.ui.base.Ui;
import com.drownedinsound.utils.EspressoIdlingResource;
import com.drownedinsound.utils.StringUtils;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    @Inject
    public BoardPostListController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler, backgroundThreadScheduler);
        this.disBoardRepo = disBoardRepo;
    }

    @Override
    public void onUiCreated(Ui ui) {
        if (ui instanceof BoardPostListParentUi) {
            BoardPostListParentUi boardPostListParentUi
                    = (BoardPostListParentUi) ui;
            int id = getId(boardPostListParentUi);
            Observable<List<BoardPostList>>
                    getBoardPostListInfoObservable = disBoardRepo.getAllBoardPostLists()
                    .compose(this.<List<BoardPostList>>defaultTransformer());

            BaseObserver<List<BoardPostList>, BoardPostListParentUi>
                    getBoardPostListInfoObserever
                    = new BaseObserver<List<BoardPostList>, BoardPostListParentUi>(id) {
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(List<BoardPostList> boardPostListInfos) {
                    getUI().setBoardPostLists(boardPostListInfos);
                }
            };
            subscribeAndCache(boardPostListParentUi, "INFO", getBoardPostListInfoObserever,
                    getBoardPostListInfoObservable);
        }
    }

    @Override
    public void onUiAttached(Ui ui) {
        if (ui instanceof BoardPostListUi) {
            BoardPostListUi boardPostListUi = (BoardPostListUi) ui;
            if (boardPostCurrentShow(boardPostListUi)) {
                requestBoardSummaryPage(boardPostListUi,
                        boardPostListUi.getBoardListType(), 1, false);
            } else {
                Timber.d(boardPostListUi.getBoardListType() + " is not currently shown so not "
                        + "upadting");
            }
        }
    }

    private boolean boardPostCurrentShow(BoardPostListUi boardPostListUi) {
        BoardPostListParentUi boardPostListParentUi = findUi(BoardPostListParentUi.class);
        if (boardPostListParentUi != null) {
            int currentPage = boardPostListParentUi.getCurrentPageShow();
            Timber.d("Current page " + currentPage + " list page index " +
                    boardPostListUi.getPageIndex());
            return currentPage == boardPostListUi.getPageIndex();
        } else {
            Timber.d("Current board post list parent UI is not attached");
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


    public void doNewNewPostAction(int currentSelectedPage) {
        if (disBoardRepo.isUserLoggedIn()) {
            BoardPostListUi boardPostListUi = findListAt(currentSelectedPage);
            if (boardPostListUi != null) {
                getDisplay().showNewPostUI(boardPostListUi.getBoardListType());
            }
        } else {
            getDisplay().showNotLoggedInUI();
        }
    }

    public void addNewPost(AddPostUI newPostUI,
            @BoardPostList.BoardPostListType String boardListType,
            String title, String content) {
        if(!StringUtils.isEmpty(title) && !StringUtils.isEmpty(content)) {
            if (!hasSubscription(newPostUI)) {
                int uiID = getId(newPostUI);
                newPostUI.showLoadingProgress(true);

                Observable<BoardPost> addPostObservable = disBoardRepo
                        .addNewPost(boardListType, title, content)
                        .compose(this.<BoardPost>defaultTransformer());

                BaseObserver<BoardPost, AddPostUI> addNewPostObserver
                        = new BaseObserver<BoardPost, AddPostUI>(uiID) {
                    @Override
                    public void onError(Throwable e) {
                        getUI().showLoadingProgress(false);
                        getUI().handleNewPostFailure();
                    }

                    @Override
                    public void onNext(BoardPost boardPost) {
                        Display display = getDisplay();
                        if (display != null) {
                            display.hideCurrentScreen();
                            @BoardPostList.BoardPostListType String boardListType
                                    = boardPost.getBoardListTypeID();
                            display.showBoardPost(boardListType,
                                    boardPost.getBoardPostID());
                        }
                    }
                };
                subscribeAndCache(newPostUI, "NEW_POST", addNewPostObserver, addPostObservable);
            }
        } else if (!StringUtils.isEmpty(title)) {
            getDisplay().showErrorMessageDialog(R.string.please_enter_some_content);
        } else if (!StringUtils.isEmpty(content)) {
            getDisplay().showErrorMessageDialog(R.string.please_enter_a_subject);
        } else {
            getDisplay().showErrorMessageDialog(R.string.please_enter_both_some_content_and_subject);
        }
    }
}
