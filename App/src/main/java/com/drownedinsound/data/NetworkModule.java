package com.drownedinsound.data;

import com.drownedinsound.core.DisBoardsAppModule;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gregmcgowan on 23/04/2016.
 */
@Module (includes = {DisBoardsAppModule.class})
public class NetworkModule {

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    private OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(5, TimeUnit.SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

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
