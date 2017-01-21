package com.drownedinsound.data;

import com.drownedinsound.core.DisBoardsAppModule;
import com.facebook.stetho.okhttp3.StethoInterceptor;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;


@Module (includes = {DisBoardsAppModule.class})
public class NetworkModule {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                .cache(cache)
                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(10, TimeUnit.SECONDS).build();

        return client;
    }


    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager(Application application) {
        return
                (ConnectivityManager) application.getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
