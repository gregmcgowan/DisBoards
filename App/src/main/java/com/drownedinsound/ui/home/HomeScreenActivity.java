package com.drownedinsound.ui.home;

import com.drownedinsound.R;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.AndroidNavigator;
import com.drownedinsound.ui.base.BaseActivity;
import com.drownedinsound.ui.home.postList.BoardPostListPresenter;
import com.drownedinsound.ui.home.postList.BoardPostListPresenterFactory;
import com.drownedinsound.ui.home.postList.BoardPostListView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class HomeScreenActivity extends BaseActivity implements HomeScreenContract.View {

    public static Intent getIntent(Context context) {
        return new Intent(context, HomeScreenActivity.class);
    }

    private static final String SAVED_TAB = "SAVED_TAB";

    @InjectView(R.id.boards_pager)
    ViewPager viewPager;

    @InjectView(R.id.board_tabs)
    TabLayout tabLayout;

    @InjectView(R.id.floating_add_button)
    FloatingActionButton floatingAddButton;

    @Inject
    HomeScreenContract.Presenter homeScreenPresenter;

    @Inject
    BoardPostListPresenterFactory boardPostListPresenterFactory;

    private HomeScreenAdapter homeScreenAdapter = new HomeScreenAdapter();

    private int currentSelectedPage;

    private AndroidNavigator androidNavigator = new AndroidNavigator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentSelectedPage = savedInstanceState.getInt(SAVED_TAB, -1);
        }

        ButterKnife.inject(this);

        homeScreenAdapter.setHomeScreenAdapterListener(
                new HomeScreenAdapter.HomeScreenAdapterListener() {
                    @Override
                    public void onBoardPostListAdded(View view, String type) {
                        BoardPostListPresenter boardPostListPresenter
                                = boardPostListPresenterFactory
                                .create(new BoardPostListView(view, type), androidNavigator);
                        homeScreenPresenter.addBoardListPresenter(type, boardPostListPresenter);
                    }

                    @Override
                    public void onBoardPostListRemoved(View view, String type) {
                        homeScreenPresenter.removeBoardPostListView(type);
                    }
                });

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

            }

            public void onPageSelected(int position) {
                handlePageSelected(position);
            }

        });
        homeScreenPresenter.onViewCreated();
    }

    private void handlePageSelected(int position) {
        String boardListType = homeScreenAdapter.getListTypeAt(position);
        homeScreenPresenter.handleListDisplayed(boardListType);
    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {
        HomeScreenComponent homeScreenComponent = sessionComponent
                .homeScreenComponent(new HomeScreenModule(this));
        homeScreenComponent.inject(this);
    }


    @Override
    public void showBoardPostLists(List<BoardPostList> boardPostLists) {
        homeScreenAdapter.setBoardPostLists(boardPostLists);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.community_layout;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        androidNavigator = null;
    }
}
