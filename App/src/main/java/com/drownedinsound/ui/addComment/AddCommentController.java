package com.drownedinsound.ui.addComment;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.utils.StringUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by gregmcgowan on 10/05/2016.
 */
public class AddCommentController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    @Inject
    public AddCommentController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler, backgroundThreadScheduler);
        this.disBoardRepo = disBoardRepo;
    }

    public void replyToComment(AddCommentUi addCommentUi,
            @BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId, String title, String content) {

        //TODO validation. Max lengths
        //Cannot be empty
        boolean valid = !StringUtils.isEmpty(title) || !StringUtils.isEmpty(content);
        if(valid) {
            int uiID = getId(addCommentUi);
            addCommentUi.showLoadingProgress(true);

            Observable<BoardPost> postCommentObservable = disBoardRepo.
                    postComment(boardListType, boardPostId, commentId, title, content)
                    .compose(this.<BoardPost>defaultTransformer());

            BaseObserver<BoardPost, AddCommentUi> postCommentObserver
                    = new BaseObserver<BoardPost, AddCommentUi>(uiID) {
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
            subscribeAndCache(addCommentUi, boardPostId, postCommentObserver,
                    postCommentObservable);
        } else {
            getDisplay().showErrorMessageDialog(R.string.please_enter_some_content);
        }
    }


}
