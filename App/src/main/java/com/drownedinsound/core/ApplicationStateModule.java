package com.drownedinsound.core;

import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.network.CookieManager;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.ui.favourites.FavouritesListFragment;
import com.drownedinsound.ui.post.BoardPostFragment;
import com.drownedinsound.ui.postList.BoardPostListFragment;
import com.drownedinsound.ui.postList.BoardPostListParentActivity;
import com.drownedinsound.ui.start.LoginActivity;
import com.drownedinsound.ui.start.StartActivity;
import com.squareup.okhttp.OkHttpClient;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;



/**
 * Created by gregmcgowan on 17/09/2014.
 */
@Module(
        injects = {
                FavouritesListFragment.class,
                DisApiClient.class,
                BoardPostFragment.class,
                BoardPostListFragment.class,
                BoardPostListParentActivity.class,
                LoginActivity.class,
                StartActivity.class,

        },
        complete = false,
        library = true
)
public class ApplicationStateModule {

    @Provides
    @Singleton
    UserSessionRepo provideUserSessionManager(CookieManager cookieManager) {
        return new UserSessionManager(cookieManager);
    }

    @Provides
    @Singleton
    CookieManager provideCookieManager(OkHttpClient okHttpClient,
            SharedPreferences sharedPreferences) {
        return new CookieManager(okHttpClient, sharedPreferences);
    }


}
