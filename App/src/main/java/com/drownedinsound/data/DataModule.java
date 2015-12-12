package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.handlers.NewPostHandler;
import com.drownedinsound.data.network.handlers.PostACommentHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.drownedinsound.data.network.handlers.ThisACommentHandler;
import com.drownedinsound.data.database.DatabaseHelper;
import com.drownedinsound.data.database.DatabaseService;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.drownedinsound.data.parser.streaming.DisWebPagerParserImpl;
import com.drownedinsound.qualifiers.ForDatabase;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.ui.post.BoardPostActivity;
import com.drownedinsound.ui.post.BoardPostFragment;
import com.drownedinsound.ui.post.PostReplyActivity;
import com.drownedinsound.ui.postList.BoardPostListFragment;
import com.drownedinsound.ui.postList.BoardPostListParentActivity;
import com.drownedinsound.ui.postList.NewPostFragment;
import com.drownedinsound.ui.start.LoginActivity;
import com.drownedinsound.ui.start.StartActivity;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import android.app.Application;
import android.content.SharedPreferences;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

@Module(
        injects = {
                NewPostHandler.class,
                RetrieveBoardPostHandler.class,
                RetrieveBoardSummaryListHandler.class,
                PostACommentHandler.class,
                ThisACommentHandler.class,
                DisApiClient.class,
                DatabaseService.class,
                StartActivity.class,
                LoginActivity.class,
                BoardPostListParentActivity.class,
                BoardPostActivity.class,
                BoardPostFragment.class,
                BoardPostListFragment.class,
                PostReplyActivity.class,
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


    @Provides
    @Singleton
    @ForIoScheduler
    public ExecutorService provideMultiThreadExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(numberCores * 2 + 1);
    }

    @Provides
    @Singleton
    @ForDatabase
    public ExecutorService provideDbExecutorService() {
        return Executors.newSingleThreadExecutor();
    }


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
    DisWebPageParser disWebPageParser() {
        return new DisWebPagerParserImpl(null, null);
    }


    @Provides
    @Singleton
    DisBoardsLocalRepo disBoardsLocalRepo(Application application) {
        return new DatabaseHelper(application.getApplicationContext());
    }

    @Provides
    @Singleton
    DisBoardRepo provideDisBoardRepo(DisApiClient disApiClient, DisBoardsLocalRepo disBoardsLocalRepo, UserSessionRepo userSessionRepo) {
        return new DisBoardRepoImpl(disApiClient,disBoardsLocalRepo,userSessionRepo);
    }

    @Provides
    @Singleton
    @ForMainThreadScheduler
    Scheduler mainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }


    @Provides
    @Singleton
    @ForIoScheduler
    Scheduler ioThreadScheduler() {
        return Schedulers.io();
    }

}
