package com.drownedinsound.ui.activity;

import com.drownedinsound.R;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Favourite Posts");
        getSupportActionBar().setIcon(null);
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


}
