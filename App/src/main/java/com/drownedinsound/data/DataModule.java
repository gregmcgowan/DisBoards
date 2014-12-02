package com.drownedinsound.data;

import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.drownedinsound.data.network.handlers.NewPostHandler;
import com.drownedinsound.data.network.handlers.PostACommentHandler;
import com.drownedinsound.data.network.handlers.ThisACommentHandler;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.database.DatabaseService;
import com.drownedinsound.qualifiers.ForDatabase;
import com.drownedinsound.qualifiers.ForNetworkRequests;
import com.drownedinsound.ui.activity.LoginActivity;
import com.drownedinsound.ui.activity.MainCommunityActivity;
import com.drownedinsound.ui.activity.StartActivity;
import com.drownedinsound.ui.fragments.BoardPostFragment;
import com.drownedinsound.ui.fragments.BoardPostSummaryListFragment;
import com.drownedinsound.ui.fragments.NewPostFragment;
import com.drownedinsound.ui.fragments.PostReplyFragment;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import android.app.Application;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module(
        injects = {
                LoginResponseHandler.class,
                NewPostHandler.class,
                RetrieveBoardPostHandler.class,
                RetrieveBoardSummaryListHandler.class,
                PostACommentHandler.class,
                ThisACommentHandler.class,
                DisApiClient.class,
                DatabaseService.class,
                StartActivity.class,
                LoginActivity.class,
                MainCommunityActivity.class,
                BoardPostFragment.class,
                BoardPostSummaryListFragment.class,
                PostReplyFragment.class,
                NewPostFragment.class
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


    @Provides @Singleton @ForNetworkRequests
    public ExecutorService provideMultiThreadExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(numberCores * 2 + 1);
    }

    @Provides @Singleton @ForDatabase
    public ExecutorService provideDbExecutorService(){
        return Executors.newSingleThreadExecutor();
    }


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    private OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(5,TimeUnit.SECONDS);
        client.setConnectTimeout(5,TimeUnit.SECONDS);
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
