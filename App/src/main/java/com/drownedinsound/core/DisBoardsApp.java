package com.drownedinsound.core;

import com.drownedinsound.BuildConfig;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.utils.CrashlyticsTree;
import com.facebook.stetho.Stetho;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import dagger.ObjectGraph;
import timber.log.Timber;

public class DisBoardsApp extends Application {

    private ObjectGraph objectGraph;

    @Inject
    DisApiClient disApiClient;

    public static DisBoardsApp getApplication(Context context) {
        return (DisBoardsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        initialiseLogging();
        initialiseDebuggingSettings();
    }

    private void initialiseDebuggingSettings() {
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(
                                    Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(
                                    Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }
    }

    private void initialiseLogging() {
        Timber.tag(DisBoardsConstants.LOG_TAG_PREFIX);
        if (BuildConfig.BUILD_TYPE.equals(BuildConfig.RELEASE_BUILD_TYPE)) {
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

    public DisApiClient getDisApiClient() {
        return disApiClient;
    }
}
