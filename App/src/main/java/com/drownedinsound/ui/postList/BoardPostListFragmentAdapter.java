package com.drownedinsound.ui.postList;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.FragmentPagerAdapter;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to handle the different board pages
 *
 * @author Greg
 */
public class BoardPostListFragmentAdapter extends FragmentPagerAdapter {

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
        return BoardPostListFragment.newInstance(listType, item);
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

    public BoardPostList getBoard(int position) {
        return boardPostLists.get(position);
    }

}
