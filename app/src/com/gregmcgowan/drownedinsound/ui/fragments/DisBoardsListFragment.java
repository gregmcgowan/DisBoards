package com.gregmcgowan.drownedinsound.ui.fragments;

import android.app.Activity;
import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;

public class DisBoardsListFragment extends SherlockListFragment {

    private boolean uiHasBeenCreated;
   
    private boolean userHint;
    
    public boolean isUserHint() {
        return userHint;
    }


    /**
     * Checks if this fragment is attached to a activity
     */
    public boolean isValid(){
	//Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Is valid ["+(getSherlockActivity() != null)+"]");
	return getSherlockActivity() != null;
    }


    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);
	uiHasBeenCreated = true;
    }


    public boolean uiHasBeenCreated() {
	Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "UI has been created ["+uiHasBeenCreated+"]");
	return uiHasBeenCreated;
    }

    public void setUIHasBeenCreated(boolean isVisibleToUser) {
	this.uiHasBeenCreated = isVisibleToUser;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
	this.userHint = isVisibleToUser;
	//Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "UserHint ["+this.userHint+"]");
	super.setUserVisibleHint(isVisibleToUser);
    }

    

    
    
    
}
