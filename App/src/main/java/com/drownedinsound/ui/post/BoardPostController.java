package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;
import com.drownedinsound.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

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
        if(disBoardRepo.isUserLoggedIn()) {
            getDisplay().showReplyUI(boardListType, postId, replyToAuthor, replyToCommentId);
        } else {
            getDisplay().showNotLoggedInUI();
        }
    }

    public void replyToComment(ReplyToCommentUi replyToCommentUi,
            @BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId, String title, String content) {

        //TODO validation. Max lengths
        //Cannot be empty
        boolean valid = !StringUtils.isEmpty(title) || !StringUtils.isEmpty(content);
        if(valid) {
            int uiID = getId(replyToCommentUi);
            replyToCommentUi.showLoadingProgress(true);

            Observable<BoardPost> postCommentObservable = disBoardRepo.
                    postComment(boardListType, boardPostId, commentId, title, content)
                    .compose(this.<BoardPost>defaultTransformer());

            BaseObserver<BoardPost, ReplyToCommentUi> postCommentObserver
                    = new BaseObserver<BoardPost, ReplyToCommentUi>(uiID) {
                @Override
                public void onError(Throwable e) {
                    getUI().showLoadingProgress(false);
                    getUI().handlePostCommentFailure();
                }

                @Override
                public void onNext(BoardPost boardPost) {
                    if(getDisplay() != null) {
                        getDisplay().hideCurrentScreen();
                    }
                }
            };
            subscribeAndCache(replyToCommentUi, boardPostId, postCommentObserver,
                    postCommentObservable);
        } else {
            getDisplay().showErrorMessageDialog(R.string.please_enter_some_content);
        }
    }

    public void thisAComment(BoardPostUI boardPostUI, @BoardPostList.BoardPostListType String boardListType,
            String postID, String commentID) {
        if(disBoardRepo.isUserLoggedIn()) {
            boardPostUI.showLoadingProgress(true);
            int id = getId(boardPostUI);

            Observable<BoardPost> thisACommentObservable = disBoardRepo
                    .thisAComment(boardListType,postID, commentID)
                    .compose(this.<BoardPost>defaultTransformer());

            BaseObserver<BoardPost,BoardPostUI> thisACommentObserver
                    = new BaseObserver<BoardPost, BoardPostUI>(id) {
                @Override
                public void onError(Throwable e) {
                    getUI().showLoadingProgress(false);
                    getUI().showThisACommentFailed();
                }

                @Override
                public void onNext(BoardPost boardPost) {
                    getUI().showBoardPost(boardPost);
                    getUI().showLoadingProgress(false);
                }
            };
            subscribeAndCache(boardPostUI, "THIS" + commentID, thisACommentObserver,
                    thisACommentObservable);
        } else {
            if(getDisplay() != null) {
                getDisplay().showNotLoggedInUI();
            }
        }
    }


}
