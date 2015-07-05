package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.ui.base.BaseActivity;
import com.drownedinsound.ui.base.BaseControllerActivity;
import com.drownedinsound.ui.base.SimpleDialogFragment;
import com.drownedinsound.ui.start.LoginActivity;
import com.drownedinsound.utils.UiUtils;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * This allows the user to move between the different message boards that are
 * available in the drowned in sound website. This is achieved through a view
 * pager
 *
 * @author Greg
 */
@UseDagger
public class BoardPostListActivity extends BaseControllerActivity<BoardPostListController> {

    private static final String LOGOUT_DIALOG = "LOGOUT_DIALOG";

    private BoardPostListFragmentAdapter mAdapter;

    @InjectView(R.id.boards_pager)
    ViewPager mPager;

    @InjectView(R.id.board_tabs)
    TabLayout tabLayout;

    @Inject
    UserSessionManager userSessionManager;

    @InjectView(R.id.floating_add_button)
    FloatingActionButton floatingAddButton;

    @Inject
    BoardPostListController boardPostListController;

    Board board;

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

        board = mAdapter.getBoard(0);

        mPager.addOnPageChangeListener(new OnPageChangeListener() {

            public void onPageScrollStateChanged(int state) {

            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            public void onPageSelected(int position) {
                Timber.d("Page selected");
                board = mAdapter.getBoard(position);
                //checkIfPageNeedsUpdating(position);
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

    @OnClick(R.id.floating_add_button)
    public void doNewPostAction() {
        Bundle newPostDetails = new Bundle();
        newPostDetails.putParcelable(DisBoardsConstants.BOARD, board);

        NewPostFragment.newInstance(newPostDetails).show(getFragmentManager(),
                "NEW_POST_DIALOG");
    }
    @Override
    protected BoardPostListController getController() {
        return boardPostListController;
    }
}
