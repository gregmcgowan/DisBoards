package com.drownedinsound.ui.postList;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
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
                        boardPostListUi.getBoardType(), 1, false);
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
                    boardPostList.getBoardType(), 1, false);
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
            @NonNull BoardType board, final int page, boolean forceUpdate) {
        String boardDisplayName = boardPostListUi.getBoardList().getDisplayName();
        String tag = boardDisplayName + "GET_LIST";
        if (!hasSubscription(boardPostListUi, tag)) {
            if (page == 1) {
                boardPostListUi.showLoadingProgress(true);
            }
            int uiId = getId(boardPostListUi);
            Timber.d("Going to update id " + uiId
                    + " for board " + boardDisplayName);
            Observable<List<BoardPost>> getBoardPostListObservable = disBoardRepo
                    .getBoardPostSummaryList(board, tag, page, forceUpdate)
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
                        public void onNext(List<BoardPost> boardPosts) {
                            if (page == 1) {
                                getUI().setBoardPosts(boardPosts);
                            } else {
                                getUI().appendBoardPosts(boardPosts);
                            }
                            getUI().showLoadingProgress(false);
                        }
                    };
            subscribeAndCache(boardPostListUi, tag, getboardPostListObserver,
                    getBoardPostListObservable);
        } else {
            Timber.d("Request for " + boardDisplayName + " already in progress");
        }
    }

    @Override
    public void onUiDetached(Ui ui) {
        if (ui instanceof BoardPostListUi) {
            BoardPostListUi boardPostListUi = ((BoardPostListUi) ui);
            Board board = boardPostListUi.getBoardList();
            if (board != null) {
                Timber.d("BoardPostListUi detached " + board.getDisplayName());
            }
            boardPostListUi.showLoadingProgress(false);
        }
    }


}
