package com.drownedinsound.ui.postList;

import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by gregmcgowan on 02/05/2016.
 */
@SingleIn(BoardPostListParentComponent.class)
public class BoardPostListParentController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    @Inject
    public BoardPostListParentController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler, backgroundThreadScheduler);
        this.disBoardRepo = disBoardRepo;
    }

    public void moveToTopOfCurrentList(BoardPostListUi boardPostListUi) {
        if (boardPostListUi != null) {
            boardPostListUi.scrollToPostAt(0);
        }
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

    public void loadList(BoardPostListUi boardPostList) {
        if (boardPostList != null) {
            boardPostList.onDisplay();
        }
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
}
