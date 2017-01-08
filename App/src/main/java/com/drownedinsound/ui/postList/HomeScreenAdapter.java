package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class HomeScreenAdapter extends PagerAdapter implements HomeScreenAdapterContract.Adapter {

    private List<BoardPostList> boardPostLists = new ArrayList<>();

    private HashMap<String, BoardPostListContract.View> boardPostListViewMap = new HashMap<>();

    @Inject
    HomeScreenAdapterContract.Presenter presenter;

    @Inject
    BoardPostListPresenterFactory boardPostListPresenterFactory;

    @Override
    public void setBoardPostLists(List<BoardPostList> boardPostLists) {
        this.boardPostLists = boardPostLists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return boardPostLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BoardPostList boardPostList = boardPostLists.get(position);

        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.board_list_layout, container, false);
        container.addView(view);

        String boardListType = boardPostList.getBoardListTypeID();
        BoardPostListView boardPostListView
                = new BoardPostListView(view, boardListType);

        boardPostListViewMap.put(boardListType, boardPostListView);

        BoardPostListPresenter boardPostListPresenter = boardPostListPresenterFactory
                .create(boardPostListView);


        view.setTag(boardListType);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        boardPostListViewMap.remove((String)(view.getTag()));
    }

    @Override
    public BoardPostListContract.View provideBoardPostListView(String listTypeId) {
        return boardPostListViewMap.get(listTypeId);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        BoardPostList boardPostListInfoInfo = boardPostLists.get(position);
        return boardPostListInfoInfo.getDisplayName();
    }

}
