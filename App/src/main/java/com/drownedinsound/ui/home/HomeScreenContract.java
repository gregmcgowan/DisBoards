package com.drownedinsound.ui.home;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.ui.home.postList.BoardPostListContract;

import java.util.List;

public interface HomeScreenContract {

    interface Presenter {

        void onViewCreated();

        void onViewDisplayed();

        void onViewHidden();

        void onViewDestroyed();

        void handleListDisplayed(String type);

        void handlePageTabReselected(int position);

        void addBoardPostListView(BoardPostListContract.View view, Display display, String type);

        void removeBoardPostListView(String type);
    }

    interface View {

        int getCurrentPageShown();

        void showBoardPostLists(List<BoardPostList> boardPostLists);
    }

}
