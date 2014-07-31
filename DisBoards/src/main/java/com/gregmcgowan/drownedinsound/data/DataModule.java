package com.gregmcgowan.drownedinsound.data;

import android.app.Application;
import android.content.SharedPreferences;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.network.NewHTTPClient;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebService;
import com.squareup.okhttp.OkHttpClient;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module(
        injects = {
                NewHTTPClient.class,
                DisWebService.class
        },
        complete = false,
        library = true
)
public class DataModule {

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("FivesOrganiser", MODE_PRIVATE);
    }

    @Provides @Singleton
    DatabaseHelper provideDatabaseHelper(Application app) {
        return new DatabaseHelper(app);
    }


    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
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

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        // Install an HTTP cache in the application cache directory.
//        try {
//            File cacheDir = new File(app.getCacheDir(), "http");
//            HttpResponseCache cache = new HttpResponseCache(cacheDir, DISK_CACHE_SIZE);
//            client.setResponseCache(cache);
//        } catch (IOException e) {
//            //Timber.e(e, "Unable to install disk cache.");
//        }

        return client;
    }
}
