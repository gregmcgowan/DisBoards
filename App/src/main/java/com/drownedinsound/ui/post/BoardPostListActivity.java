package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.ui.base.BaseActivity;
import com.drownedinsound.ui.base.SimpleDialogFragment;
import com.drownedinsound.ui.start.LoginActivity;
import com.drownedinsound.utils.UiUtils;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
public class BoardPostListActivity extends BaseActivity {

    private static final String LOGOUT_DIALOG = "LOGOUT_DIALOG";

    private BoardPostListFragmentAdapter mAdapter;

    @InjectView(R.id.boards_pager)
    ViewPager mPager;

    @InjectView(R.id.board_tabs)
    TabLayout tabLayout;

    @Inject
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        mAdapter = new BoardPostListFragmentAdapter(getFragmentManager(),
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

        tabLayout.setupWithViewPager(mPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mPager.addOnPageChangeListener(new OnPageChangeListener() {

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
                BoardPostListFragment boardPostListFragment
                        = getListFragment(position);
                if (boardPostListFragment != null) {
                    boardPostListFragment.loadListIfNotAlready();
                }
            }


        });
    }

    private BoardPostListFragment getListFragment(int position) {
        BoardPostListFragment boardPostListFragment = null;
        String fragmentName = UiUtils.makeFragmentPagerAdapterTagName(
                R.id.boards_pager, position);
        Fragment fragment = getFragmentManager()
                .findFragmentByTag(fragmentName);
        if (fragment instanceof BoardPostListFragment) {
            boardPostListFragment
                    = (BoardPostListFragment) fragment;

        }
        return boardPostListFragment;
    }

    @OnClick(R.id.profile_button)
    public void profileButtonPressed() {
        if (userSessionManager.isUserLoggedIn()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SimpleDialogFragment existingLogoutDialog =
                    (SimpleDialogFragment) fragmentManager.findFragmentByTag(LOGOUT_DIALOG);
            if (existingLogoutDialog != null) {
                fragmentTransaction.remove(existingLogoutDialog);
            }

            SimpleDialogFragment logoutDialog =
                    SimpleDialogFragment.newInstance(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                doLogoutAction();
                            }
                        }
                    }, "", "Do you want to logout?", "Yes", "No");

            logoutDialog.show(fragmentTransaction, LOGOUT_DIALOG);

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
