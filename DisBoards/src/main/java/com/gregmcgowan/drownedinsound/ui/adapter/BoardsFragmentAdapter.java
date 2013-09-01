package com.gregmcgowan.drownedinsound.ui.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;

/**
 * Adapter to handle the different board pages
 * 
 * @author Greg
 * 
 */
public class BoardsFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Board> boards;

    public BoardsFragmentAdapter(FragmentManager fm,DatabaseHelper databaseHelper) {
	super(fm);
	boards = databaseHelper.getCachedBoards();
    }

    @Override
    public Fragment getItem(int item) {
	Board boardInfo = boards.get(item);
	boolean firstPage = item == 0;
	return BoardPostSummaryListFragment.newInstance(boardInfo, firstPage);
    }

    @Override
    public int getCount() {
	return boards.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
	Board boardInfo = boards.get(position);
	return boardInfo.getDisplayName();
    }

}
