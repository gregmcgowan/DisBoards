package com.gregmcgowan.drownedinsound.core;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.ObjectGraph;

public class DisBoardsApp extends Application {

    private static final String MULTI_THREADED_EXECUTOR_SERVICE = "MULTI_THREADED_EXECUTOR_SERVICE";

    private static final float EXECUTOR_POOL_SIZE_PER_CORE = 1.5F;

    private final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "App";

    private ExecutorService multiThreadedExecutorService;

    private ObjectGraph objectGraph;


    public static DisBoardsApp getApplication(Context context) {
        return (DisBoardsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
    }

    public ExecutorService getMultiThreadedExecutorService() {
        if (null == multiThreadedExecutorService
                || multiThreadedExecutorService.isShutdown()) {
            final int numThreads = Math.round(Runtime.getRuntime()
                    .availableProcessors() * EXECUTOR_POOL_SIZE_PER_CORE);
            multiThreadedExecutorService = Executors.newFixedThreadPool(
                    numThreads, new DisBoardsThreadFactory(
                            MULTI_THREADED_EXECUTOR_SERVICE));

            if (DisBoardsConstants.DEBUG) {
                Log.d(TAG, "MultiThreadExecutor created with " + numThreads
                        + " threads");
            }
        }
        return multiThreadedExecutorService;
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
