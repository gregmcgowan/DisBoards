package com.drownedinsound.ui.post;

import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 02/10/15.
 */
@SingleIn(BoardPostComponent.class)
public class BoardPostController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    @Inject
    public BoardPostController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler,backgroundThreadScheduler);
        this.disBoardRepo = disBoardRepo;
    }

    public void loadBoardPost(BoardPostUI boardPostUI, @BoardPostList.BoardPostListType String boardListType,
            final String boardPostId, boolean force) {
        int uiID = getId(boardPostUI);
        if(!hasSubscription(boardPostUI,boardPostId)) {
            boardPostUI.showLoadingProgress(true);

            Observable<BoardPost> getBoardPostObservable =
                    Observable.zip(disBoardRepo.getBoardPost(boardListType, boardPostId, force),
                disBoardRepo.getBoardPostSummary(boardListType, boardPostId),
                    new Func2<BoardPost, BoardPostSummary, BoardPost>() {
                        @Override
                        public BoardPost call(BoardPost boardPost,
                                BoardPostSummary boardPostSummary) {
                            if (boardPostSummary != null) {
                                boardPost.setNumberOfTimesOpened(
                                        boardPostSummary.getNumberOfTimesOpened());
                            }

                            return boardPost;
                        }
                    }).compose(this.<BoardPost>defaultTransformer());

            BaseObserver<BoardPost,BoardPostUI> getBoardPostObserver = new BaseObserver<BoardPost,BoardPostUI>(uiID) {
                @Override
                public void onError(Throwable e) {
                    getUI().showErrorView();
                    getUI().showLoadingProgress(false);
                }

                @Override
                public void onNext(BoardPost boardPost) {
                    final BoardPostUI postUI = getUI();
                    postUI.showBoardPost(boardPost);

                    Timber.d("Number of times opened "+boardPost.getNumberOfTimesOpened());
                    if (boardPost.getNumberOfTimesOpened() > 1
                            && boardPost.getNumberOfReplies() > 0
                            && !postUI.userHasInteractedWithUI()) {
                        postUI.setOnContentShownListener(
                                new DisBoardsLoadingLayout.ContentShownListener() {
                                    @Override
                                    public void onContentShown() {
                                        if (!postUI.lastCommentIsVisible()) {
                                            postUI.showGoToLatestCommentOption();
                                        }
                                        postUI.setOnContentShownListener(null);
                                    }
                                });
                    }
                    postUI.showLoadingProgress(false);

                }
            };
            subscribeAndCache(boardPostUI, boardPostId, getBoardPostObserver,
                    getBoardPostObservable);
        }
    }


    public void showReplyUI(@BoardPostList.BoardPostListType String boardListType,
            String postId, String replyToAuthor, String replyToCommentId) {
        getDisplay().showFeatureExpiredUI();
    }


    public void thisAComment(BoardPostUI boardPostUI, @BoardPostList.BoardPostListType String boardListType,
            String postID, String commentID) {
        getDisplay().showFeatureExpiredUI();
    }


}
