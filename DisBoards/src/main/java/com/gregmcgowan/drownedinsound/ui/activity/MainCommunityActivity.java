package com.gregmcgowan.drownedinsound.ui.activity;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gregmcgowan.drownedinsound.DisBoardsApp;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.DatabaseService;
import com.gregmcgowan.drownedinsound.data.model.NavigationDrawerItem;
import com.gregmcgowan.drownedinsound.events.LoginSucceededEvent;
import com.gregmcgowan.drownedinsound.ui.adapter.BoardsFragmentAdapter;
import com.gregmcgowan.drownedinsound.ui.adapter.NavigationDrawerAdapter;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;
import com.gregmcgowan.drownedinsound.utils.UiUtils;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * This allows the user to move between the different message boards that are
 * available in the drowned in sound website. This is achieved through a view
 * pager
 *
 * @author Greg
 */
public class MainCommunityActivity extends SherlockFragmentActivity {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "MainCommunityActivity";

    private BoardsFragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;

    private DrawerLayout navigationDrawerLayout;
    private ListView navigationDrawerListView;
    private NavigationDrawerAdapter navigationDrawerAdapter;
    private ActionBarDrawerToggle navigationDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private final ArrayList<NavigationDrawerItem> navigationDrawerItems = new ArrayList<NavigationDrawerItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_layout);
        EventBus.getDefault().register(this);
        mAdapter = new BoardsFragmentAdapter(getSupportFragmentManager(),
            DatabaseHelper.getInstance(getApplicationContext()));

        initialiseViewPager();
        initialiseSlidingDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initialiseSlidingDrawer() {
        navigationDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawerListView = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        navigationDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        initialiseNavigationDrawerItems();

        navigationDrawerAdapter = new NavigationDrawerAdapter(this,R.layout.navigation_drawer_list_item, navigationDrawerItems);
        navigationDrawerListView.setAdapter(navigationDrawerAdapter);
        navigationDrawerListView.setOnItemClickListener(new DrawerItemClickListener(new WeakReference<MainCommunityActivity>(this)));

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

            navigationDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                navigationDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
            R.string.drawer_open,  /* "open drawer" description for accessibility */
               R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
           public void onDrawerClosed(View view) {
               super.onDrawerClosed(view);
               invalidateOptionsMenu();
           }

           public void onDrawerOpened(View drawerView) {
               getSupportActionBar().setTitle(mDrawerTitle);
               invalidateOptionsMenu();
           }
       };
        navigationDrawerLayout.setDrawerListener(navigationDrawerToggle);

    }

    private void initialiseNavigationDrawerItems(){
        boolean loggedIn = DisBoardsApp.getApplication(this).userIsLoggedIn();
        if(!loggedIn) {
            navigationDrawerItems.add(new LoginDrawerItem(this));
        }
        //navigationDrawerItems.add(new NavigationDrawerItem("Boards"));
        navigationDrawerItems.add(new FavouritesDrawerItem(this));
        navigationDrawerItems.add(new ProfileDrawerItem(this));
        navigationDrawerItems.add(new MessagesDrawerItem(this));
        navigationDrawerItems.add(new SettingsDrawerItem(this));
        if(loggedIn) {
            navigationDrawerItems.add(new LogoutDrawerItem(this));
        }
    }


    private void initialiseViewPager() {
        mPager = (ViewPager) findViewById(R.id.boards_pager);
        mPager.setAdapter(mAdapter);
        mPager.getCurrentItem();
        mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
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
                Log.d(TAG, "Checking if page  " + position + " needs updating");
                String fragmentName = UiUtils.makeFragmentPagerAdapterTagName(
                    R.id.boards_pager, position);
                Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(fragmentName);
                if (fragment instanceof BoardPostSummaryListFragment) {
                    BoardPostSummaryListFragment listFragment = (BoardPostSummaryListFragment) fragment;
                    listFragment.loadListIfNotAlready();
                }
            }


        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
        checkForUpdates();
    }

    private void checkForCrashes() {
        CrashManager.register(this, DisBoardsConstants.HOCKEY_APP_ID);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, DisBoardsConstants.HOCKEY_APP_ID);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        navigationDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        navigationDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean menuVisibilty = !navigationDrawerLayout.isDrawerOpen(navigationDrawerListView);
        int numberOfMenuItems = menu.size();
        for(int i = 0; i < numberOfMenuItems; i++) {
            MenuItem item = menu.getItem(i);
            item.setEnabled(menuVisibilty);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (navigationDrawerLayout.isDrawerOpen(navigationDrawerListView)) {
                navigationDrawerLayout.closeDrawer(navigationDrawerListView);
            } else {
                navigationDrawerLayout.openDrawer(navigationDrawerListView);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO rename
    private void updateAfterLoginOrLogout(){
        navigationDrawerLayout.postDelayed( new Runnable() {
            @Override
            public void run() {

               navigationDrawerItems.clear();
               initialiseNavigationDrawerItems();
               navigationDrawerAdapter.notifyDataSetChanged();
            }
        },500);

    }

    public void onEventMainThread(LoginSucceededEvent event) {
        updateAfterLoginOrLogout();
    }

    public void doNavigationDrawerAction(int position){
        NavigationDrawerItem navigationDrawerItem = navigationDrawerItems.get(position);
        if(navigationDrawerItem != null) {
            navigationDrawerItem.doNavigationDrawerItemSelectedAction();
        }
        if (navigationDrawerLayout.isDrawerOpen(navigationDrawerListView)) {
            navigationDrawerLayout.closeDrawer(navigationDrawerListView);
        }
    }

    public void doLogoutAction(){
        //Might need to do a proper logout
        DisBoardsApp.getApplication(this).clearCookies();
        updateAfterLoginOrLogout();
    }

    public void doViewFavouritesAction(){
        Intent displayFavouritesIntent = new Intent(this,FavouritesActivity.class);
        startActivity(displayFavouritesIntent);
    }


    private static class DrawerItemClickListener implements ListView.OnItemClickListener {

        private WeakReference<MainCommunityActivity> mainCommunityActivityWeakReference;

        public DrawerItemClickListener(WeakReference<MainCommunityActivity> mainCommunityActivityWeakReference){
            this.mainCommunityActivityWeakReference = mainCommunityActivityWeakReference;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mainCommunityActivityWeakReference != null) {
                MainCommunityActivity mainCommunityActivity = mainCommunityActivityWeakReference.get();
                if(mainCommunityActivity != null) {
                    mainCommunityActivity.doNavigationDrawerAction(position);
                }
            }
        }
    }

    public static class LoginDrawerItem extends  NavigationDrawerItem {

        public LoginDrawerItem(Context context){
           super("Login",new WeakReference<Context>(context));
        }

        @Override
        public void doNavigationDrawerItemSelectedAction() {
            Context context = getContext();
            if(context != null) {
                Intent loginIntent = new Intent(context,LoginActivity.class);
                context.startActivity(loginIntent);
            }
        }

    }

    public static class LogoutDrawerItem extends  NavigationDrawerItem {

        public LogoutDrawerItem(Context context){
            super("Logout",new WeakReference<Context>(context));
        }

        @Override
        public void doNavigationDrawerItemSelectedAction() {
            Context context = getContext();
            if(context != null) {
                MainCommunityActivity mainCommunityActivity = (MainCommunityActivity)context;
                mainCommunityActivity.doLogoutAction();
            }
        }

    }

    public static class FavouritesDrawerItem extends NavigationDrawerItem {

        public FavouritesDrawerItem(Context context) {
            super("Favourites", new WeakReference<Context>(context));
        }

        @Override
        public void doNavigationDrawerItemSelectedAction() {
            Context context = getContext();
            if(context != null) {
                MainCommunityActivity mainCommunityActivity = (MainCommunityActivity)context;
                mainCommunityActivity.doViewFavouritesAction();
            }
        }
    }


    public static class ProfileDrawerItem extends  NavigationDrawerItem {

        public ProfileDrawerItem(Context context){
            super("Profile",new WeakReference<Context>(context));
        }

        @Override
        public void doNavigationDrawerItemSelectedAction() {
            Context context = getContext();
            if(context != null) {
                MainCommunityActivity mainCommunityActivity = (MainCommunityActivity)context;
            }
        }

    }

    public static class MessagesDrawerItem extends  NavigationDrawerItem {

        public MessagesDrawerItem(Context context){
            super("Messages",new WeakReference<Context>(context));
        }

        @Override
        public void doNavigationDrawerItemSelectedAction() {
            Context context = getContext();
            if(context != null) {
                MainCommunityActivity mainCommunityActivity = (MainCommunityActivity)context;
            }
        }

    }

    public static class SettingsDrawerItem extends  NavigationDrawerItem {

        public SettingsDrawerItem(Context context){
            super("Settings",new WeakReference<Context>(context));
        }

        @Override
        public void doNavigationDrawerItemSelectedAction() {
            Context context = getContext();
            if(context != null) {
                MainCommunityActivity mainCommunityActivity = (MainCommunityActivity)context;
            }
        }

    }


}
