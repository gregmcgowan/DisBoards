package com.drownedinsound.ui.post;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

public interface BoardPostContract {

    interface View {
        void showLoadingProgress(boolean show);

        void showBoardPost(BoardPost boardPost);

        void showErrorView();

        void showThisACommentFailed();

        void showGoToLatestCommentOption();

        void setOnContentShownListener(DisBoardsLoadingLayout.ContentShownListener contentShownListener);

        boolean lastCommentIsVisible();

        boolean userHasInteractedWithUI();
    }

    interface Presenter {
        void onViewCreated();

        void onViewDestroyed();
    }

}
