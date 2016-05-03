package com.drownedinsound.core;

import com.crashlytics.android.Crashlytics;
import com.drownedinsound.BuildConfig;
import com.drownedinsound.data.DataModule;
import com.drownedinsound.utils.CrashlyticsTree;
import com.facebook.stetho.Stetho;

import android.app.Application;
import android.content.Context;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class DisBoardsApp extends Application {

    public static DisBoardsApp getApplication(Context context) {
        return (DisBoardsApp) context.getApplicationContext();
    }

    private AppComponent appComponent;

    private SessionComponent sessionComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initialiseLogging();
        initialiseDebuggingSettings();
        createAppComponent();

        if (BuildConfig.BUILD_TYPE.equals("beta")
                || BuildConfig.BUILD_TYPE.equals("release")) {
            Fabric.with(this,new Crashlytics());
        }
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

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void createAppComponent() {
        this.appComponent = AppComponent.Initialiser.init(this);
        this.sessionComponent = appComponent.provideSessionComponent(new DataModule());
    }

    public SessionComponent getSessionComponent() {
        return sessionComponent;
    }
}
