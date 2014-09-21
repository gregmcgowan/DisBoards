package com.gregmcgowan.drownedinsound.data;

import com.gregmcgowan.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.ui.activity.LoginActivity;
import com.gregmcgowan.drownedinsound.ui.activity.MainCommunityActivity;
import com.gregmcgowan.drownedinsound.ui.activity.StartActivity;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import android.app.Application;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module(
        injects = {
                LoginResponseHandler.class,
                RetrieveBoardPostHandler.class,
                DisWebService.class,
                DatabaseService.class,
                MainCommunityActivity.class,
                LoginActivity.class,
                StartActivity.class
        },
        complete = false,
        library = true
)
public class DataModule {

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("DisBoards", MODE_PRIVATE);
    }

    @Provides
    @Singleton
    DatabaseHelper provideDatabaseHelper(Application app) {
        return new DatabaseHelper(app);
    }


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }


    private OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            //Timber.e(e, "Unable to install disk cache.");
        }

        return client;
    }

    //
//    @Provides @Singleton
//    Picasso providePicasso(Application app, OkHttpClient client) {
//        return new Picasso.Builder(app)
//                .downloader(new OkHttpDownloader(client))
//                .listener(new Picasso.Listener() {
//                    @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
//                       // Debug.log("Image load failed for "+uri);
//                    }
//                })
//                .build();
//    }
}
