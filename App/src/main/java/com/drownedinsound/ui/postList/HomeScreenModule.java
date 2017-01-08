package com.drownedinsound.ui.postList;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module(includes = {})
public class HomeScreenModule {

    private final HomeScreenAdapterContract.Adapter homeScreenAdapter;

    private final HomeScreenContract.View homeScreenView;

    public HomeScreenModule(
            HomeScreenAdapterContract.Adapter homeScreenAdapter,
            HomeScreenContract.View homeScreenView) {
        this.homeScreenAdapter = homeScreenAdapter;
        this.homeScreenView = homeScreenView;
    }


    @Provides
    HomeScreenContract.Presenter providePresenter(
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler,
            HomeScreenAdapterContract.Presenter homescreenAdpaterPresenter,
            DisBoardRepo disBoardRepo) {
        return new HomeScreenPresenter(homeScreenView, homescreenAdpaterPresenter, disBoardRepo,
                mainThreadScheduler, backgroundThreadScheduler);
    }

    @Provides
    HomeScreenAdapterContract.Presenter provideHomeScreenAdapterPresenter() {
        return new HomeScreenAdapterPresenter(homeScreenAdapter);
    }

    @Provides
    BoardPostListContract.View getView (String type) {
        return homeScreenAdapter.provideBoardPostListView(type);
    }



}
