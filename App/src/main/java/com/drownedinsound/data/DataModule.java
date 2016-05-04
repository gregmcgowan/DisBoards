package com.drownedinsound.data;

import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepoImpl;
import com.drownedinsound.data.generatered.DaoMaster;
import com.drownedinsound.data.network.CookieManager;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.NetworkUtil;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.squareup.okhttp.OkHttpClient;

import android.content.SharedPreferences;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gregmcgowan on 04/05/2016.
 */
@Module(includes = {})
public class DataModule {


    @Provides
    @SingleIn(SessionComponent.class)
    UserSessionRepo provideUserSessionManager(AppPreferences appPreferences,
            CookieManager cookieManager) {
        return new UserSessionManager(appPreferences, cookieManager);
    }

    @Provides
    @SingleIn(SessionComponent.class)
    CookieManager provideCookieManager(OkHttpClient okHttpClient,
            @Named("Cookies") SharedPreferences sharedPreferences) {
        return new CookieManager(okHttpClient, sharedPreferences);
    }

    @Provides
    @SingleIn(SessionComponent.class)
    BoardPostSummaryListParser postSummaryListParser(UserSessionRepo userSessionRepo) {
        return new BoardPostSummaryListParser(userSessionRepo);
    }

    @Provides
    @SingleIn(SessionComponent.class)
    BoardPostParser provideBoardPostParser(UserSessionRepo userSessionRepo) {
        return new BoardPostParser(userSessionRepo);
    }
    @Provides
    @SingleIn(SessionComponent.class)
    DisBoardsLocalRepo disBoardsLocalRepo(DaoMaster daoMaster) {
        return new DisBoardsLocalRepoImpl(daoMaster.newSession());
    }

    @Provides
    @SingleIn(SessionComponent.class)
    DisApiClient disApiClient(OkHttpClient okHttpClient, NetworkUtil networkUtil,
            DisWebPageParser disWebPageParser) {
        return new DisApiClient(okHttpClient, networkUtil, disWebPageParser);
    }
}
