package com.drownedinsound.ui.home;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.home.postList.BoardPostListContract;

import android.support.annotation.NonNull;

import java.util.List;

public interface HomeScreenContract {

    interface Presenter {

        void onViewCreated();

        void handleListDisplayed(@NonNull String type);

        void handlePageTabReselected(int position);

        void addBoardListPresenter(@NonNull String type, @NonNull BoardPostListContract.Presenter presenter);

        void removeBoardPostListView(@NonNull String type);
    }

    interface View {

        void showBoardPostLists(List<BoardPostList> boardPostLists);
    }

}
