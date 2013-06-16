package com.gregmcgowan.drownedinsound.ui.activity;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.ui.adapter.BoardsFragmentAdapter;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;
import com.gregmcgowan.drownedinsound.utils.UiUtils;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * This allows the user to move between the different message boards that are
 * available in the drowned in sound website. This is achieved through a view
 * pager
 * 
 * @author Greg
 * 
 */
public class MainCommunityActivity extends SherlockFragmentActivity {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "MainCommunityActivity";
    
    private BoardsFragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.community_layout);

	mAdapter = new BoardsFragmentAdapter(getSupportFragmentManager(),
		DatabaseHelper.getInstance(getApplicationContext()));

	mPager = (ViewPager) findViewById(R.id.boards_pager);
	mPager.setAdapter(mAdapter);
	mPager.getCurrentItem();
	mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
	mIndicator.setViewPager(mPager);

	mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

	    public void onPageScrollStateChanged(int state) {
		if(state == ViewPager.SCROLL_STATE_DRAGGING) {
		    	int currentPage = mPager.getCurrentItem();
		    	int maxPages = mAdapter.getCount();
		    	int pageToLeft = currentPage -1;
		    	int pageToRight = currentPage + 1;
		    	
		    	if(pageToLeft > -1) {
		    	    checkIfPageNeedsUpdating(pageToLeft);
		    	} 
		    	if(pageToRight < maxPages) {
		    	    checkIfPageNeedsUpdating(pageToRight);
		    	}
		}
	    }

	    public void onPageScrolled(int arg0, float arg1, int arg2) {

	    }

	    public void onPageSelected(int position) {
		checkIfPageNeedsUpdating(position);
	    }
	    
	    private void checkIfPageNeedsUpdating(int position){
		Log.d(TAG, "Checking if page  "+position +" needs updating");
		String fragmentName = UiUtils.makeFragmentPagerAdapterTagName(
			R.id.boards_pager, position);
		Fragment fragment = getSupportFragmentManager()
			.findFragmentByTag(fragmentName);
		if (fragment instanceof BoardPostSummaryListFragment) {
		    BoardPostSummaryListFragment listFragment = (BoardPostSummaryListFragment) fragment;
		    listFragment.loadListIfNotAlready();
		}
	    }
	    
	    
	});
    }
    
    @Override
    public void onResume() {
      super.onResume();
      checkForCrashes();
      checkForUpdates();
    }

    private void checkForCrashes() {
      CrashManager.register(this, DisBoardsConstants.HOCKEY_APP_ID);
    }

    private void checkForUpdates() {
      // Remove this for store builds!
      UpdateManager.register(this, DisBoardsConstants.HOCKEY_APP_ID);
    }


}
