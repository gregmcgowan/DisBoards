package com.drownedinsound.ui.post;


import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.ui.base.Navigator;

import android.support.annotation.NonNull;

import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func2;

import static rx.Observable.zip;

public class BoardPostPresenter implements BoardPostContract.Presenter {

    private final String boardPostId;

    private final String boardListType;

    private final BoardPostContract.View boardPostView;

    private final Navigator navigator;

    private final DisBoardRepo disBoardRepo;

    private final Scheduler mainThreadScheduler;

    private final Scheduler backgroundThreadScheduler;

    private Subscription loadBoardPostSubscription;

    public BoardPostPresenter(
            String postID,
            String boardListType,
            BoardPostContract.View boardPostView,
            Navigator navigator,
            Scheduler mainThreadScheduler,
            Scheduler backgroundThreadScheduler,
            DisBoardRepo disBoardRepo) {
        this.boardPostId = postID;
        this.boardListType = boardListType;
        this.boardPostView = boardPostView;
        this.navigator = navigator;
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
    }


    @Override
    public void onViewCreated() {
        loadBoardPost(false);
    }

    private void loadBoardPost(boolean forceUpdate) {
        boardPostView.showLoadingProgress(true);
        loadBoardPostSubscription = zip(
                disBoardRepo.getBoardPost(boardListType, boardPostId, forceUpdate),
                disBoardRepo.getBoardPostSummary(boardListType, boardPostId),
                getUpdateNumberOfTimesOpened())
                .subscribeOn(backgroundThreadScheduler)
                .observeOn(mainThreadScheduler)
                .subscribe(new Observer<BoardPost>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        boardPostView.showErrorView();
                        boardPostView.showLoadingProgress(false);
                    }

                    @Override
                    public void onNext(BoardPost boardPost) {
                        boardPostView.showBoardPost(boardPost);
                        boardPostView.showLoadingProgress(false);
                    }
                });
    }

    @NonNull
    private Func2<BoardPost, BoardPostSummary, BoardPost> getUpdateNumberOfTimesOpened() {
        return new Func2<BoardPost, BoardPostSummary, BoardPost>() {
            @Override
            public BoardPost call(BoardPost boardPost,
                    BoardPostSummary boardPostSummary) {
                if (boardPostSummary != null) {
                    boardPost.setNumberOfTimesOpened(
                            boardPostSummary.getNumberOfTimesOpened());
                }

                return boardPost;
            }
        };
    }

    @Override
    public void onViewDestroyed() {
        if(loadBoardPostSubscription != null) {
            loadBoardPostSubscription.unsubscribe();
        }
    }
}
