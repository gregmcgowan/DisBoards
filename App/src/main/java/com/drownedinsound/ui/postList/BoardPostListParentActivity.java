package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.model.BoardPostListInfo;
import com.drownedinsound.ui.base.BaseControllerActivity;
import com.drownedinsound.ui.base.SimpleDialogFragment;
import com.drownedinsound.ui.favourites.FavouritesActivity;
import com.drownedinsound.ui.start.LoginActivity;

import android.app.FragmentTransaction;
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

    BoardPostListInfo boardPostListInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        mAdapter = new BoardPostListFragmentAdapter(getFragmentManager());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.community_layout;
    }

    private void initialiseViewPager() {
        tabLayout.setupWithViewPager(mPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mPager.addOnPageChangeListener(new OnPageChangeListener() {

            public void onPageScrollStateChanged(int state) {

            }

            public void onPageScrolled(int state, float positionOffset, int positionPixels) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    int currentPage = mPager.getCurrentItem();
                    int maxPages = mAdapter.getCount();
                    int pageToLeft = currentPage - 1;
                    int pageToRight = currentPage + 1;

                    if (pageToLeft > -1) {
                        boardPostListController.loadListAt(pageToLeft);
                    }
                    if (pageToRight < maxPages) {
                        boardPostListController.loadListAt(pageToRight);
                    }
                }
            }

            public void onPageSelected(int position) {
                boardPostListInfo = mAdapter.getBoard(position);
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
    public void setBoardPostLists(List<BoardPostListInfo> boardPostListInfos) {
        mAdapter.setBoardPostListInfos(boardPostListInfos);
        mPager.setAdapter(mAdapter);
        initialiseViewPager();
        boardPostListInfo = mAdapter.getBoard(0);
    }

    @Override
    public int getNoOfBoardListShown() {
        return mAdapter.getCount();
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
        Bundle newPostDetails = new Bundle();
        //newPostDetails.putParcelable(DisBoardsConstants.BOARD, board);

        NewPostFragment.newInstance(newPostDetails).show(getFragmentManager(),
                "NEW_POST_DIALOG");
    }

    @Override
    protected BoardPostListController getController() {
        return boardPostListController;
    }

    @Override
    public boolean boardPostListShown(BoardPostListUi boardPostListUi) {
        return mPager.getCurrentItem() == boardPostListUi.getPageIndex();
    }
}
