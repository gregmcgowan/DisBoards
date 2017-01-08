package com.drownedinsound.ui.postList;

import com.drownedinsound.data.generatered.BoardPostList;

import java.util.List;

public class HomeScreenAdapterPresenter implements HomeScreenAdapterContract.Presenter {

    private final HomeScreenAdapterContract.Adapter adapter;

    public HomeScreenAdapterPresenter(HomeScreenAdapterContract.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void showBoardPostLists(List<BoardPostList> boardPostLists) {
        adapter.setBoardPostLists(boardPostLists);
    }
}
