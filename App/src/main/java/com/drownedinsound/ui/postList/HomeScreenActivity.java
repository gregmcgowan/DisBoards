package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class HomeScreenActivity extends BaseActivity implements HomeScreenContract.View {

    private static final String SAVED_TAB = "SAVED_TAB";

    @InjectView(R.id.boards_pager)
    ViewPager viewPager;

    @InjectView(R.id.board_tabs)
    TabLayout tabLayout;

    @InjectView(R.id.floating_add_button)
    FloatingActionButton floatingAddButton;

    @Inject
    HomeScreenContract.Presenter homeScreenPresenter;

    private HomeScreenAdapter homeScreenAdapter = new HomeScreenAdapter();

    private int currentSelectedPage;

    public static Intent getIntent(Context context) {
        return new Intent(context,HomeScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            currentSelectedPage = savedInstanceState.getInt(SAVED_TAB,-1);
        }

        ButterKnife.inject(this);

        viewPager.setAdapter(homeScreenAdapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //currentSelectedPage = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                homeScreenPresenter.handlePageTabReselected(tab.getPosition());
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int state) {

            }

            public void onPageScrolled(int state, float positionOffset, int positionPixels) {
//                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
//                    int currentPage = viewPager.getCurrentItem();
//                    int maxPages = boardPostListFragmentAdapter.getCount();
//                    int pageToLeft = currentPage - 1;
//                    int pageToRight = currentPage + 1;
//
//                    if (pageToLeft > -1) {
//                        //boardPostListController.loadListAt(pageToLeft);
//                    }
//                    if (pageToRight < maxPages) {
//                        //boardPostListController.loadListAt(pageToRight);
//                    }
//                }
            }

            public void onPageSelected(int position) {
                Timber.d("Page selected " + position);
//                currentSelectedPage = position;
//                BoardPostListFragment boardPostListFragment = boardPostListFragmentAdapter
//                        .getBoardPostListFragment(position);
//                boardPostListController.loadList(boardPostListFragment);
            }

        });

        homeScreenPresenter.onViewCreated();
    }

    @Override
    public void showBoardPostLists(List<BoardPostList> boardPostLists) {
        homeScreenAdapter.setBoardPostLists(boardPostLists);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {
        HomeScreenComponent homeScreenComponent = sessionComponent
                .homeScreenComponent(new HomeScreenModule(homeScreenAdapter, this));

        homeScreenComponent.inject(this);
        homeScreenComponent.inject(homeScreenAdapter);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.community_layout;
    }

    @Override
    public int getCurrentPageShown() {
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
