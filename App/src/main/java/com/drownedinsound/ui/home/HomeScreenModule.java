package com.drownedinsound.ui.home;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module(includes = {})
public class HomeScreenModule {

    private final HomeScreenContract.View homeScreenView;

    public HomeScreenModule(
            HomeScreenContract.View homeScreenView) {
        this.homeScreenView = homeScreenView;
    }


    @Provides
    HomeScreenContract.Presenter providePresenter(
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler, DisBoardRepo disBoardRepo) {
        return new HomeScreenPresenter(homeScreenView, disBoardRepo,
                mainThreadScheduler, backgroundThreadScheduler);
    }


}