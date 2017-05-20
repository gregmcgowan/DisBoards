package com.drownedinsound.ui.home;


import com.drownedinsound.core.SingleIn;
import com.drownedinsound.ui.home.postList.BoardPostListPresenterFactory;

import dagger.Binds;
import dagger.BindsInstance;
import dagger.Module;
import dagger.Subcomponent;

@SingleIn(HomeScreenComponent.class)
@Subcomponent (modules = HomeScreenComponent.HomeScreenModule.class)
public interface HomeScreenComponent {

    void inject(HomeScreenActivity homeScreenActivity);

    BoardPostListPresenterFactory providePresenterFactory();

    @Module
    interface HomeScreenModule {
        @Binds
        HomeScreenContract.Presenter providePresenter(HomeScreenPresenter homeScreenPresenter);
    }

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        Builder homeScreenView(HomeScreenContract.View homescreenView);

        HomeScreenComponent build();
    }
}
