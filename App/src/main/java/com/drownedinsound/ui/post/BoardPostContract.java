package com.drownedinsound.ui.post;

import com.drownedinsound.BoardPostItem;

import android.support.annotation.NonNull;

import java.util.List;

public interface BoardPostContract {

    interface View {
        
        void showLoadingProgress(boolean show);

        void showBoardPostItems(@NonNull List<BoardPostItem> items);

        void showErrorView();

        void setPresenter(Presenter presenter);
    }

    interface Presenter {

        void onViewCreated();

        void onViewDestroyed();

        void handleBackAction();

        void handleRefreshAction();
    }

}
