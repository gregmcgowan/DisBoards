package com.drownedinsound.ui.postList;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.SmartFragmentStatePagerAdapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to handle the different board pages
 *
 * @author Greg
 */
public class BoardPostListFragmentAdapter extends SmartFragmentStatePagerAdapter {

    private List<BoardPostList> boardPostLists = new ArrayList<>();

    public BoardPostListFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setBoardPostListInfos(List<BoardPostList> boardPostListInfos) {
        this.boardPostLists = boardPostListInfos;
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int item) {
        BoardPostList boardPostListInfoInfo = boardPostLists.get(item);
        @BoardPostList.BoardPostListType String listType = boardPostListInfoInfo.getBoardListTypeID();
        BoardPostListFragment boardPostListFragment = BoardPostListFragment.newInstance(listType, item);
        return boardPostListFragment;
    }



    public BoardPostListFragment getBoardPostListFragment(int position) {
        return (BoardPostListFragment) super.getRegisteredFragment(position);
    }

    @Override
    public int getCount() {
        return boardPostLists.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        BoardPostList boardPostListInfoInfo = boardPostLists.get(position);
        return boardPostListInfoInfo.getDisplayName();
    }

}
