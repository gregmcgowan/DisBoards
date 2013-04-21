package com.gregmcgowan.drownedinsound.ui.fragments;

import com.actionbarsherlock.app.SherlockFragment;

public class DisBoardsFragment extends SherlockFragment {
    
    /**
     * Checks if this fragment is attached to a activity
     */
    public boolean isValid(){
	return getSherlockActivity() != null;
    }
}
