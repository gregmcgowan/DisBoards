package com.drownedinsound.ui.activity;

import com.drownedinsound.ui.fragments.FavouritesListFragment;

import android.os.Bundle;

/**
 * Created by gregmcgowan on 29/10/2013.
 */
public class FavouritesActivity extends DisBoardsActivity {

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


}
