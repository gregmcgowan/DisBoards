package com.gregmcgowan.drownedinsound.core;

import com.gregmcgowan.drownedinsound.BuildConfig;
import com.gregmcgowan.drownedinsound.utils.CrashlyticsTree;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.ObjectGraph;
import timber.log.Timber;

public class DisBoardsApp extends Application {

    private ObjectGraph objectGraph;

    public static DisBoardsApp getApplication(Context context) {
        return (DisBoardsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        initialiseLogging();
    }

    private void initialiseLogging() {
        Timber.tag(DisBoardsConstants.LOG_TAG_PREFIX);
        if(BuildConfig.BUILD_TYPE.equals(BuildConfig.RELEASE_BUILD_TYPE)) {
            Timber.plant(new CrashlyticsTree());
        } else {
            Timber.plant(new Timber.DebugTree());
        }
    }


    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
