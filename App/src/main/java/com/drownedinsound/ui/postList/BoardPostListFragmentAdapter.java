package com.drownedinsound.ui.postList;

import com.drownedinsound.data.model.BoardPostListInfo;
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

    private List<BoardPostListInfo> boardPostListInfos = new ArrayList<>();

    public BoardPostListFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setBoardPostListInfos(List<BoardPostListInfo> boardPostListInfos) {
        this.boardPostListInfos = boardPostListInfos;
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int item) {
        BoardPostListInfo boardPostListInfoInfo = boardPostListInfos.get(item);
        return BoardPostListFragment.newInstance(boardPostListInfoInfo.getBoardListType(),item);
    }

    @Override
    public int getCount() {
        return boardPostListInfos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        BoardPostListInfo boardPostListInfoInfo = boardPostListInfos.get(position);
        return boardPostListInfoInfo.getDisplayName();
    }

    public BoardPostListInfo getBoard(int position) {
        return boardPostListInfos.get(position);
    }

}
