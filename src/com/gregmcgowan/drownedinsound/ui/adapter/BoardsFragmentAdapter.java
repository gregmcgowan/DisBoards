package com.gregmcgowan.drownedinsound.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.model.BoardTypeConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardTypeInfo;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;

/**
 * Adapter to handle the different board pages
 * 
 * @author Greg
 * 
 */
public class BoardsFragmentAdapter extends FragmentPagerAdapter {

    // TODO might allow the user to reorder the order they are displayed in so
    // this approach will not work
    private static final BoardTypeInfo[] BOARDS = new BoardTypeInfo[] {
	    new BoardTypeInfo(BoardType.MUSIC,
		    BoardTypeConstants.MUSIC_DISPLAY_NAME,
		    UrlConstants.MUSIC_URL),
	    new BoardTypeInfo(BoardType.SOCIAL,
		    BoardTypeConstants.SOCIAL_DISPLAY_NAME,
		    UrlConstants.SOCIAL_URL),
	    new BoardTypeInfo(BoardType.ANNOUNCEMENTS_CLASSIFIEDS,
		    BoardTypeConstants.ANNOUNCEMENTS_CLASSIFIEDS_DISPLAY_NAME,
		    UrlConstants.ANNOUNCEMENTS_CLASSIFIEDS_URL),
	    new BoardTypeInfo(BoardType.MUSICIANS,
		    BoardTypeConstants.MUSICIANS_DISPLAY_NAME,
		    UrlConstants.MUSICIANS_URL),
	    new BoardTypeInfo(BoardType.FESTIVALS,
		    BoardTypeConstants.FESTIVALS_DISPLAY_NAME,
		    UrlConstants.FESTIVALS_URL),
	    new BoardTypeInfo(BoardType.YOUR_MUSIC,
		    BoardTypeConstants.YOUR_MUSIC_DISPLAY_NAME,
		    UrlConstants.YOUR_MUSIC_URL),
	    new BoardTypeInfo(BoardType.ERRORS_SUGGESTIONS,
		    BoardTypeConstants.ERROR_SUGGESTIONS_DISPLAY_NAME,
		    UrlConstants.ERRORS_SUGGESTIONS_URL) };

    public BoardsFragmentAdapter(FragmentManager fm) {
	super(fm);
    }

    @Override
    public Fragment getItem(int item) {
	BoardTypeInfo boardInfo = BOARDS[item];
	boolean firstPage = item == 0;
	return BoardPostSummaryListFragment.newInstance(boardInfo, firstPage);
    }

    @Override
    public int getCount() {
	return BOARDS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
	BoardTypeInfo boardInfo = BOARDS[position];
	return boardInfo.getDisplayName();
    }

}
