package com.drownedinsound.ui.post;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.events.FailedToGetBoardPostEvent;
import com.drownedinsound.events.PostCommentEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;


import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by gregmcgowan on 02/10/15.
 */
@Singleton
public class BoardPostController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    private Scheduler mainThreadScheduler;

    private Scheduler backgroundThreadScheduler;

    @Inject
    public BoardPostController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        this.disBoardRepo = disBoardRepo;
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;
    }

    public void loadBoardPost(BoardPostUI boardPostUI, @BoardPostList.BoardPostListType String boardListType,
            String boardPostId, boolean force) {
        int uiID = getId(boardPostUI);
        if(!hasSubscription(boardPostUI,boardPostId)) {
            boardPostUI.showLoadingProgress(true);

            Observable<BoardPost> getBoardPostObservable = disBoardRepo
                    .getBoardPost(boardListType,boardPostId,force)
                    .subscribeOn(backgroundThreadScheduler)
                    .observeOn(mainThreadScheduler);

            BaseObserver<BoardPost,BoardPostUI> getBoardPostObserver = new BaseObserver<BoardPost,BoardPostUI>(uiID) {
                @Override
                public void onError(Throwable e) {
                    getUI().showErrorView();
                    getUI().showLoadingProgress(false);
                }

                @Override
                public void onNext(BoardPost boardPost) {
                    getUI().showBoardPost(boardPost,-1);
                    getUI().showLoadingProgress(false);
                }
            };
            subscribeAndCache(boardPostUI,boardPostId,getBoardPostObserver,getBoardPostObservable);
        }
    }

    public void thisAComment(BoardPostUI boardPostUI, @BoardPostList.BoardPostListType String boardListType,
            String postID, String commentID) {
        boardPostUI.showLoadingProgress(true);
        int id = getId(boardPostUI);
        //disApiClient.thisAComment(postUrl, postID, commentID, boardType,id  );
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(RetrievedBoardPostEvent event) {
        int callingID = event.getUiId();

        BoardPostUI boardPostUI = (BoardPostUI) findUi(callingID);
        if (boardPostUI != null) {
            BoardPost boardPost = event.getBoardPost();

//            boolean showGoToLastCommentOption = event
//                    .isDisplayGotToLatestCommentOption()
//                    && boardPost.();

            boardPostUI.showBoardPost(boardPost, -1);

            if (event.isCached()) {
                boardPostUI.showCachedPopup();
            }
            boardPostUI.showLoadingProgress(false);
//            if (showGoToLastCommentOption) {
//                hideAnimatedLogoAndShowList(new OnListShownHandler() {
//                    @Override
//                    public void doOnListShownAction() {
//                        displayScrollToHiddenCommentOption(true);
//                    }
//                });
//
//            } else {
//                hideAnimatedLogoAndShowList(new OnListShownHandler() {
//                    @Override
//                    public void doOnListShownAction() {
//                        //floatingReplyButton.show(true);
//                    }
//                });
//            }

        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(FailedToGetBoardPostEvent failedToGetBoardPostEvent) {
        BoardPostUI boardPostUI = (BoardPostUI) findUi(failedToGetBoardPostEvent.getCallingId());
        if (boardPostUI != null) {
            boardPostUI.showErrorView();
        }
    }

    public void replyToComment(ReplyToCommentUi replyToCommentUi,String boardPostId, String replyToCommentID, String commentTitle,
            String commentContent, @BoardPostList.BoardPostListType String boardListType) {
        replyToCommentUi.showLoadingProgress(true);

        int uiID = getId(replyToCommentUi);

        //disApiClient.postComment(boardPostId,replyToCommentID,commentTitle,commentContent,boardType);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PostCommentEvent postCommentEvent) {
       // eventBus.removeStickyEvent(postCommentEvent);
        ReplyToCommentUi replyToCommentUi = (ReplyToCommentUi) findUi(postCommentEvent.getUiID());
        if(replyToCommentUi != null) {
            if(postCommentEvent.isSuccess()) {
                replyToCommentUi.hidePostCommentUi();
            } else {
                replyToCommentUi.handlePostCommentFailure();
            }

        }

    }
}
