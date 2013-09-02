package com.gregmcgowan.drownedinsound.ui.fragments;

import com.actionbarsherlock.app.SherlockListFragment;

public class DisBoardsListFragment extends SherlockListFragment {


    /**
     * Checks if this fragment is attached to a activity
     */
    public boolean isValid() {
        //Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Is valid ["+(getSherlockActivity() != null)+"]");
        return getSherlockActivity() != null;
    }


}
