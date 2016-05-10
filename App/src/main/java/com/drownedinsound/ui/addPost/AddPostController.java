package com.drownedinsound.ui.addPost;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.utils.StringUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by gregmcgowan on 10/05/2016.
 */
public class AddPostController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    @Inject
    public AddPostController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler, backgroundThreadScheduler);
        this.disBoardRepo = disBoardRepo;
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
