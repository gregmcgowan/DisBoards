package com.gregmcgowan.drownedinsound.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;

/**
 * Adapter to handle the different board pages
 * 
 * @author Greg
 * 
 */
public class BoardsFragmentAdapter extends FragmentPagerAdapter {

    private static final BoardInfo[] BOARDS = new BoardInfo[] {
	    new BoardInfo("Social", UrlConstants.SOCIAL_URL),
	    new BoardInfo("Music", UrlConstants.MUSIC_URL) };

    public BoardsFragmentAdapter(FragmentManager fm) {
	super(fm);
    }

    @Override
    public Fragment getItem(int item) {
	BoardInfo boardInfo = BOARDS[item];
	boolean firstPage = item == 0;
	return BoardPostSummaryListFragment.newInstance(boardInfo.url,
		boardInfo.title, firstPage);
    }

    @Override
    public int getCount() {
	return BOARDS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
	BoardInfo boardInfo = BOARDS[position];
	return boardInfo.title;
    }

    private static class BoardInfo {
	private String title;
	private String url;

	public BoardInfo(String title, String url) {
	    this.title = title;
	    this.url = url;
	}

    }

}
