package com.gregmcgowan.drownedinsound.core;

import com.gregmcgowan.drownedinsound.data.DataModule;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
@Module(
        includes = {
                DataModule.class,
                ApplicationStateModule.class
        },
        injects = {
                DisBoardsApp.class
        }
)

public class DisBoardsModule {

    private final DisBoardsApp app;

    public DisBoardsModule(DisBoardsApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }
}
