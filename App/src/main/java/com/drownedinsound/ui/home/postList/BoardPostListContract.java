package com.drownedinsound.ui.home.postList;

import com.drownedinsound.BoardPostListModel;
import com.drownedinsound.data.generatered.BoardPostList;

import android.support.annotation.NonNull;

import java.util.List;

public interface BoardPostListContract {

    interface Presenter {

        void onViewCreated();

        void onViewDisplayed();

        void onViewDestroyed();

        void handleRefresh();

        void handleBoardPostSelected(BoardPostListModel boardPostListModel);
    }

    interface View {

        void setPresenter(Presenter presenter);

        @BoardPostList.BoardPostListType String getBoardListType();

        void showBoardPostSummaries(@NonNull List<BoardPostListModel> boardPostsSummaries);

        void showLoadingProgress(boolean show);

        void showErrorView();
    }

}
