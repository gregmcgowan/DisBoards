package com.drownedinsound.core;

import android.app.Application;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module()
public class DisBoardsAppModule {

    public static final String MAIN_THREAD_SCHEDULER = "MAIN_THREAD_SCHEDULER";
    public static final String BACKGROUND_THREAD_SCHEDULER = "BACKGROUND_THREAD_SCHEDULER";

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
    @Named(MAIN_THREAD_SCHEDULER)
    Scheduler mainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }


    @Provides
    @Singleton
    @Named(BACKGROUND_THREAD_SCHEDULER)
    Scheduler ioThreadScheduler() {
        return Schedulers.io();
    }
}
