package com.gregmcgowan.drownedinsound.core;

import android.app.Application;
import android.content.SharedPreferences;

import com.gregmcgowan.drownedinsound.data.DatabaseService;
import com.gregmcgowan.drownedinsound.data.UserSessionManager;
import com.gregmcgowan.drownedinsound.data.network.CookieManager;
import com.gregmcgowan.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.ui.activity.LoginActivity;
import com.gregmcgowan.drownedinsound.ui.activity.MainCommunityActivity;
import com.gregmcgowan.drownedinsound.ui.activity.StartActivity;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostFragment;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostSummaryListFragment;
import com.gregmcgowan.drownedinsound.ui.fragments.DisBoardsFragment;
import com.gregmcgowan.drownedinsound.ui.fragments.FavouritesListFragment;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;



/**
 * Created by gregmcgowan on 17/09/2014.
 */
@Module(
        injects = {
                LoginResponseHandler.class,
                RetrieveBoardSummaryListHandler.class,
                RetrieveBoardPostHandler.class,
                FavouritesListFragment.class,
                DisWebService.class,
                DatabaseService.class,
                BoardPostFragment.class,
                BoardPostSummaryListFragment.class,
                MainCommunityActivity.class,
                LoginActivity.class,
                StartActivity.class
        },
        complete = false,
        library = true
)
public class ApplicationStateModule {

    @Provides
    @Singleton
    UserSessionManager provideUserSessionManager(CookieManager cookieManager){
        return new UserSessionManager(cookieManager);
    }

    @Provides @Singleton
    CookieManager provideCookieManager(OkHttpClient okHttpClient, SharedPreferences sharedPreferences){
        return new CookieManager(okHttpClient,sharedPreferences);
    }

    @Provides @Singleton
    EventBus provideEventBus(){
        return new EventBus();
    }

}
