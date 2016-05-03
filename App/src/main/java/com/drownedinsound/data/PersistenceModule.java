package com.drownedinsound.data;

import com.drownedinsound.core.DisBoardsAppModule;
import com.drownedinsound.data.database.DisBoardsDataBaseHelper;
import com.drownedinsound.data.generatered.DaoMaster;

import android.app.Application;
import android.content.SharedPreferences;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gregmcgowan on 23/04/2016.
 */

@Module(includes = {DisBoardsAppModule.class})
public class PersistenceModule {

    public static final String DIS_DB = "dis.db";

    @Provides
    @Named("Cookies") @Singleton
    SharedPreferences provideCookieSharedPreference(Application app) {
        return app.getSharedPreferences("DisBoardsCookies", MODE_PRIVATE);
    }

    @Provides
    @Named("AppState") @Singleton
    SharedPreferences provideAppSharedPreferences(Application app) {
        return app.getSharedPreferences("DisBoardsAppPreferences", MODE_PRIVATE);
    }

    @Provides @Singleton
    DaoMaster provideDaoMaster(Application application) {
        return new DaoMaster(new DisBoardsDataBaseHelper(application.getApplicationContext(),
                DIS_DB,null).getWritableDatabase());
    }
}
