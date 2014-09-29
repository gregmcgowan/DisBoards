package com.gregmcgowan.drownedinsound.core;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.ObjectGraph;

public class DisBoardsApp extends Application {

    private ObjectGraph objectGraph;

    public static DisBoardsApp getApplication(Context context) {
        return (DisBoardsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
    }


    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
