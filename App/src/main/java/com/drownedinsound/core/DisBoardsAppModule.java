package com.drownedinsound.core;

import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
@Module()
public class DisBoardsAppModule {

    private final DisBoardsApp app;

    public DisBoardsAppModule(DisBoardsApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    @ForMainThreadScheduler
    Scheduler mainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }


    @Provides
    @Singleton
    @ForIoScheduler
    Scheduler ioThreadScheduler() {
        return Schedulers.io();
    }
}
