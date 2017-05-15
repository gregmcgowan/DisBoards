package com.drownedinsound.ui.post;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.Navigator;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module(includes = {})
public class BoardPostModule {

    private final BoardPostContract.View boardPostView;

    private final String postId;

    private final String boardPostType;

    private final Navigator navigator;

    BoardPostModule(
            @NonNull Navigator navigator,
            @NonNull BoardPostContract.View boardPostView,
            @NonNull String postId,
            @NonNull String boardPostType) {
        this.navigator = navigator;
        this.boardPostView = boardPostView;
        this.postId = postId;
        this.boardPostType = boardPostType;
    }

    @Provides
    BoardPostContract.Presenter providePresenter(
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler,
            @NonNull DisBoardRepo disBoardRepo,
            @NonNull BoardPostModelMapper boardPostModelMapper) {
        return new BoardPostPresenter(postId,
                boardPostType,
                boardPostView,
                navigator,
                mainThreadScheduler,
                backgroundThreadScheduler, disBoardRepo, boardPostModelMapper);
    }
}
