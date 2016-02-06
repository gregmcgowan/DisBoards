package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseControllerActivity;
import com.drownedinsound.ui.base.SimpleDialogFragment;
import com.drownedinsound.ui.favourites.FavouritesActivity;
import com.drownedinsound.ui.start.LoginActivity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import java.util.List;

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
public class BoardPostListParentActivity extends BaseControllerActivity<BoardPostListController>
        implements BoardPostListParentUi {

    private static final String LOGOUT_DIALOG = "LOGOUT_DIALOG";

    private static final String SAVED_TAB = "SAVED_TAB";


    @InjectView(R.id.boards_pager)
    ViewPager viewPager;

    @InjectView(R.id.board_tabs)
    TabLayout tabLayout;

    @Inject
    UserSessionManager userSessionManager;

    @InjectView(R.id.floating_add_button)
    FloatingActionButton floatingAddButton;

    @Inject
    BoardPostListController boardPostListController;

    private BoardPostListFragmentAdapter boardPostListFragmentAdapter;

    private int currentSelectedPage;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,BoardPostListParentActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            currentSelectedPage = savedInstanceState.getInt(SAVED_TAB,-1);
        }
        ButterKnife.inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.community_layout;
    }

    private void initialiseViewPager() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentSelectedPage = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            public void onPageScrollStateChanged(int state) {

            }

            public void onPageScrolled(int state, float positionOffset, int positionPixels) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    int currentPage = viewPager.getCurrentItem();
                    int maxPages = boardPostListFragmentAdapter.getCount();
                    int pageToLeft = currentPage - 1;
                    int pageToRight = currentPage + 1;

                    if (pageToLeft > -1) {
                        //boardPostListController.loadListAt(pageToLeft);
                    }
                    if (pageToRight < maxPages) {
                        //boardPostListController.loadListAt(pageToRight);
                    }
                }
            }

            public void onPageSelected(int position) {
                currentSelectedPage = position;
                boardPostListController.loadListAt(position);
            }

        });
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

    @Override
    public void setBoardPostLists(List<BoardPostList> boardPostListInfos) {
        boardPostListFragmentAdapter = new BoardPostListFragmentAdapter(getFragmentManager());
        boardPostListFragmentAdapter.setBoardPostListInfos(boardPostListInfos);
        viewPager.setAdapter(boardPostListFragmentAdapter);
        initialiseViewPager();
    }


    public void doLogoutAction() {
        //TODO Might need to do a proper logout
        userSessionManager.clearSession();
    }

    public void doViewFavouritesAction() {
        Intent displayFavouritesIntent = new Intent(this, FavouritesActivity.class);
        startActivity(displayFavouritesIntent);
    }

    @OnClick(R.id.floating_add_button)
    public void doNewPostAction() {
        boardPostListController.doNewNewPostAction(currentSelectedPage);
    }

    @Override
    protected BoardPostListController getController() {
        return boardPostListController;
    }

    @Override
    public int getCurrentPageShow() {
        return currentSelectedPage;
    }

    @Override
    public void onPause() {
        super.onPause();
        currentSelectedPage = viewPager.getCurrentItem();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentSelectedPage != -1 && viewPager.getCurrentItem() != currentSelectedPage) {
            viewPager.setCurrentItem(currentSelectedPage);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_TAB, currentSelectedPage);

    }
}
