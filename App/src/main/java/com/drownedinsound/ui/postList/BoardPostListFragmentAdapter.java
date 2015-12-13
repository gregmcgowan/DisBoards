package com.drownedinsound.ui.postList;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.database.DatabaseHelper;
import com.drownedinsound.ui.base.FragmentPagerAdapter;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.ArrayList;

/**
 * Adapter to handle the different board pages
 *
 * @author Greg
 */
public class BoardPostListFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Board> boards;

    public BoardPostListFragmentAdapter(FragmentManager fm, DatabaseHelper databaseHelper) {
        super(fm);
        boards = databaseHelper.getCachedBoards();
    }

    @Override
    public Fragment getItem(int item) {
        Board boardInfo = boards.get(item);
        return BoardPostListFragment.newInstance(boardInfo.getBoardType());
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

    public Board getBoard(int position) {
        return boards.get(position);
    }

}
