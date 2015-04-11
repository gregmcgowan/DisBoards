package com.drownedinsound.ui.summarylist;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.ui.base.FragmentPagerAdapter;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.ArrayList;

/**
 * Adapter to handle the different board pages
 *
 * @author Greg
 */
public class BoardPostSummaryListFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Board> boards;

    public BoardPostSummaryListFragmentAdapter(FragmentManager fm, DatabaseHelper databaseHelper) {
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
