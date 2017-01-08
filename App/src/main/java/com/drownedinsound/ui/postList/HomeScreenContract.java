package com.drownedinsound.ui.postList;

import com.drownedinsound.data.generatered.BoardPostList;

import java.util.List;

public interface HomeScreenContract {

    interface Presenter {

        void onViewCreated();

        void onViewDisplayed();

        void onViewHidden();

        void onViewDestroyed();

        void handleListDisplayed(int pageIndex);

        void handlePageTabReselected(int position);
    }

    interface View {

        int getCurrentPageShown();

        void showBoardPostLists(List<BoardPostList> boardPostLists);
    }

}
