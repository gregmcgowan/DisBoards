package com.drownedinsound.ui.activity;

import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.drownedinsound.ui.fragments.BoardPostSummaryListFragment;
import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.data.DatabaseHelper;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.ui.adapter.BoardsFragmentAdapter;
import com.drownedinsound.utils.UiUtils;
import com.viewpagerindicator.PageIndicator;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This allows the user to move between the different message boards that are
 * available in the drowned in sound website. This is achieved through a view
 * pager
 *
 * @author Greg
 */
@UseDagger @UseEventBus
public class MainCommunityActivity extends DisBoardsActivity {

    private BoardsFragmentAdapter mAdapter;

    @InjectView(R.id.swipeToRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

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
        swipeRefreshLayout.setColorSchemeColors(R.color.highlighted_blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BoardPostSummaryListFragment boardPostSummaryListFragment
                        = getListFragment(mPager.getCurrentItem());
                if(boardPostSummaryListFragment != null) {
                    boardPostSummaryListFragment.doRefreshAction();
                }
            }
        });
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

    public void doLogoutAction() {
        //Might need to do a proper logout
        userSessionManager.clearSession();
    }

    public void doViewFavouritesAction() {
        Intent displayFavouritesIntent = new Intent(this, FavouritesActivity.class);
        startActivity(displayFavouritesIntent);
    }

    public void onEventMainThread(RetrievedBoardPostSummaryListEvent event) {
        swipeRefreshLayout.setRefreshing(false);
    }



}
