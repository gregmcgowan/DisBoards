package com.gregmcgowan.drownedinsound.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardConstants;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;

/**
 * Adapter to handle the different board pages
 * 
 * @author Greg
 * 
 */
public class BoardsFragmentAdapter extends FragmentPagerAdapter {


    public BoardsFragmentAdapter(FragmentManager fm) {
	super(fm);
    }

    @Override
    public Fragment getItem(int item) {
	Board boardInfo = BoardConstants.BOARDS.get(item);
	boolean firstPage = item == 0;
	return BoardPostSummaryListFragment.newInstance(boardInfo, firstPage);
    }

    @Override
    public int getCount() {
	return BoardConstants.BOARDS.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
	Board boardInfo =BoardConstants.BOARDS.get(position);
	return boardInfo.getDisplayName();
    }

}
