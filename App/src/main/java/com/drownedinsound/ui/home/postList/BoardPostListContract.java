package com.drownedinsound.ui.home.postList;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;

import java.util.List;

public interface BoardPostListContract {

    interface Presenter {

        void onViewCreated();

        void onViewDisplayed();

        void onViewHidden();

        void onViewDestroyed();

        void handleRefresh();

        void handleBoardPostSelected(BoardPostSummary boardPostSummary);
    }

    interface View {

        void setPresenter(Presenter presenter);

        @BoardPostList.BoardPostListType String getBoardListType();

        void showBoardPostSummaries(List<BoardPostSummary> boardPostsSummaries);

        void showLoadingProgress(boolean show);

        void showErrorView();

        void scrollToPostAt(int position);
    }

}
