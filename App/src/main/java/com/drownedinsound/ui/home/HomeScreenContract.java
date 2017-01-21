package com.drownedinsound.ui.home;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.home.postList.BoardPostListContract;

import java.util.List;

public interface HomeScreenContract {

    interface Presenter {

        void onViewCreated();

        void handleListDisplayed(String type);

        void handlePageTabReselected(int position);

        void addBoardListPresenter(String type, BoardPostListContract.Presenter presenter);

        void removeBoardPostListView(String type);
    }

    interface View {

        void showBoardPostLists(List<BoardPostList> boardPostLists);
    }

}
