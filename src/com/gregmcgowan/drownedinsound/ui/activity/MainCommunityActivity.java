package com.gregmcgowan.drownedinsound.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crittercism.app.Crittercism;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.ui.adapter.BoardsFragmentAdapter;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;
import com.gregmcgowan.drownedinsound.utils.UiUtils;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * This allows the user to move between the different message boards that
 * are available in the drowned in sound website. This is achieved through
 * a view pager 
 * 
 * @author Greg
 *
 */
public class MainCommunityActivity extends SherlockFragmentActivity {

    private BoardsFragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.community_layout);
	Crittercism.init(getApplicationContext(), DisBoardsConstants.CRITTERCISM_APP_ID);
	
	mAdapter = new BoardsFragmentAdapter(getSupportFragmentManager());

	mPager = (ViewPager) findViewById(R.id.boards_pager);
	mPager.setAdapter(mAdapter);
	mPager.getCurrentItem();
	mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
	mIndicator.setViewPager(mPager);
	
	mIndicator.setOnPageChangeListener(new  OnPageChangeListener(){

	    public void onPageScrollStateChanged(int arg0) {
		
	    }

	    public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	    }

	    public void onPageSelected(int position) {
		String fragmentName = UiUtils.makeFragmentPagerAdapterTagName(R.id.boards_pager, position);
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
		if(fragment instanceof BoardPostSummaryListFragment){
		    BoardPostSummaryListFragment listFragment = (BoardPostSummaryListFragment)fragment;
		    listFragment.loadListIfNotAlready(1);
		}
	    }
	    
	});
    }


 
    
    
    
    
}
