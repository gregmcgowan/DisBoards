package com.drownedinsound.ui.favourites;

import com.drownedinsound.R;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.ui.base.BaseActivity;

import android.os.Bundle;

/**
 * Created by gregmcgowan on 29/10/2013.
 */
public class FavouritesActivity extends BaseActivity {

    private FavouritesListFragment favouritesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Favourite Posts");
        getActionBar().setIcon(null);
        if (savedInstanceState == null) {
            favouritesListFragment = new FavouritesListFragment();
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, favouritesListFragment).commit();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.community_layout;
    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {

    }
}
