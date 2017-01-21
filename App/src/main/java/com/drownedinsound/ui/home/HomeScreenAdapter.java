package com.drownedinsound.ui.home;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenAdapter extends PagerAdapter {

    private List<BoardPostList> boardPostLists = new ArrayList<>();

    private HomeScreenAdapterListener homeScreenAdapterListener;

    public void setBoardPostLists(List<BoardPostList> boardPostLists) {
        this.boardPostLists = boardPostLists;
        notifyDataSetChanged();
    }

    public void setHomeScreenAdapterListener(HomeScreenAdapterListener homeScreenAdapterListener) {
        this.homeScreenAdapterListener = homeScreenAdapterListener;
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
        view.setTag(boardListType);

        if (homeScreenAdapterListener != null) {
            homeScreenAdapterListener.onBoardPostListAdded(view, boardListType);
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        String boardListType = (String) view.getTag();
        container.removeView(view);

        if (homeScreenAdapterListener != null) {
            homeScreenAdapterListener.onBoardPostListRemoved(view, boardListType);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        BoardPostList boardPostListInfoInfo = boardPostLists.get(position);
        return boardPostListInfoInfo.getDisplayName();
    }

    public String getListTypeAt(int postion){
        return boardPostLists.get(postion).getBoardListTypeID();
    }

    interface HomeScreenAdapterListener {

        void onBoardPostListAdded(View view, String type);

        void onBoardPostListRemoved(View view, String type);
    }

}
