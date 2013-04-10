package com.gregmcgowan.drownedinsound.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostFragment;
import com.gregmcgowan.drownedinsound.utils.UiUtils;

/**
 * This will contain a board post fragment. A board post will be made of the
 * original post and comments. This activity will not do much apart from load
 * the fragment
 * 
 * @author Greg
 * 
 */
public class BoardPostActivity extends SherlockFragmentActivity {
    
    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostActivity";

    private FragmentManager fragmentManager;

    private BoardPostFragment boardPostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	fragmentManager = getSupportFragmentManager();

	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
	int screenWidthDp = UiUtils.convertPixelsToDp(getResources(), screenWidthPixels);
	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && 
		screenWidthDp >= UiUtils.MIN_WITH_DP_FOR_DUAL_MODE){
	    // If the screen is now in landscape mode, we can show the
	    // dialog in-line with the list so we don't need this activity.
	    finish();
	    return;
	}

	if (savedInstanceState == null) {
	    // During initial setup, plug in the details fragment.
	    boardPostFragment = new BoardPostFragment();
	    boardPostFragment.setArguments(getIntent().getExtras());
	    fragmentManager.beginTransaction()
		    .add(android.R.id.content, boardPostFragment).commit();
	}

    }

   public void removeBoardPostFragment(){
       finish();
   }


}
