package com.drownedinsound.ui.activity;

import com.drownedinsound.ui.fragments.BoardPostSummaryListFragment;
import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.data.DatabaseHelper;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.ui.adapter.BoardsFragmentAdapter;
import com.drownedinsound.ui.fragments.SimpleDialogFragment;
import com.drownedinsound.utils.UiUtils;
import com.viewpagerindicator.PageIndicator;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * This allows the user to move between the different message boards that are
 * available in the drowned in sound website. This is achieved through a view
 * pager
 *
 * @author Greg
 */
@UseDagger
public class MainCommunityActivity extends DisBoardsActivity {

    private static final String LOGOUT_DIALOG = "LOGOUT_DIALOG";

    private BoardsFragmentAdapter mAdapter;

    @InjectView(R.id.boards_pager)
    ViewPager mPager;

    PageIndicator mIndicator;

    @Inject
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        mAdapter = new BoardsFragmentAdapter(getFragmentManager(),
                DatabaseHelper.getInstance(getApplicationContext()));

        initialiseViewPager();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.community_layout;
    }

    private void initialiseViewPager() {
        mPager.setAdapter(mAdapter);
        mPager.getCurrentItem();

        mIndicator = (PageIndicator) findViewById(R.id.boards_pager_indicator);
        mIndicator.setViewPager(mPager);

        mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    int currentPage = mPager.getCurrentItem();
                    int maxPages = mAdapter.getCount();
                    int pageToLeft = currentPage - 1;
                    int pageToRight = currentPage + 1;

                    if (pageToLeft > -1) {
                        checkIfPageNeedsUpdating(pageToLeft);
                    }
                    if (pageToRight < maxPages) {
                        checkIfPageNeedsUpdating(pageToRight);
                    }
                }
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            public void onPageSelected(int position) {
                checkIfPageNeedsUpdating(position);
            }

            private void checkIfPageNeedsUpdating(int position) {
                BoardPostSummaryListFragment boardPostSummaryListFragment
                        = getListFragment(position);
                if(boardPostSummaryListFragment != null) {
                    boardPostSummaryListFragment.loadListIfNotAlready();
                }
            }


        });
    }

    private BoardPostSummaryListFragment getListFragment(int position) {
        BoardPostSummaryListFragment boardPostSummaryListFragment = null;
        String fragmentName = UiUtils.makeFragmentPagerAdapterTagName(
                R.id.boards_pager, position);
        Fragment fragment = getFragmentManager()
                .findFragmentByTag(fragmentName);
        if (fragment instanceof BoardPostSummaryListFragment) {
            boardPostSummaryListFragment
                    = (BoardPostSummaryListFragment) fragment;

        }
        return boardPostSummaryListFragment;
    }

    @OnClick(R.id.profile_button)
    public void profileButtonPressed(){
        if(userSessionManager.isUserLoggedIn()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SimpleDialogFragment existingLogoutDialog =
                    (SimpleDialogFragment) fragmentManager.findFragmentByTag(LOGOUT_DIALOG);
            if(existingLogoutDialog != null) {
                fragmentTransaction.remove(existingLogoutDialog);
            }

            SimpleDialogFragment logoutDialog =
                    SimpleDialogFragment.newInstance(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == DialogInterface.BUTTON_POSITIVE) {
                        doLogoutAction();
                    }
                }
            },"","Do you want to logout?","Yes","No");

            logoutDialog.show(fragmentTransaction,LOGOUT_DIALOG);

        } else {
            Intent startLoginActivity = new Intent(this,
                    LoginActivity.class);
            startActivity(startLoginActivity);
        }
    }


    public void doLogoutAction() {
        //Might need to do a proper logout
        userSessionManager.clearSession();
    }

    public void doViewFavouritesAction() {
        Intent displayFavouritesIntent = new Intent(this, FavouritesActivity.class);
        startActivity(displayFavouritesIntent);
    }



}
