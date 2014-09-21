package com.gregmcgowan.drownedinsound.ui.activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.gregmcgowan.drownedinsound.ui.fragments.FavouritesListFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

/**
 * Created by gregmcgowan on 29/10/2013.
 */
public class FavouritesActivity extends SherlockFragmentActivity {

    private FragmentManager fragmentManager;

    private FavouritesListFragment favouritesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getSupportFragmentManager();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Favourite Posts");
        getSupportActionBar().setIcon(null);
        if (savedInstanceState == null) {
            favouritesListFragment = new FavouritesListFragment();
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, favouritesListFragment).commit();
        }
    }


}
