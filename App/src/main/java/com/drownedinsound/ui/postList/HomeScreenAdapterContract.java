package com.drownedinsound.ui.postList;

import com.drownedinsound.data.generatered.BoardPostList;

import java.util.List;

public class HomeScreenAdapterContract {

    interface Presenter  {

        void showBoardPostLists(List<BoardPostList> boardPostLists);
    }

    interface Adapter {

        void setBoardPostLists(List<BoardPostList> boardPostLists);

        BoardPostListContract.View provideBoardPostListView(String listTypeId);
    }

}
