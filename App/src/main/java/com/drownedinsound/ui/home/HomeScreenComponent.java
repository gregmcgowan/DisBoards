package com.drownedinsound.ui.home;

import com.drownedinsound.core.SingleIn;
import com.drownedinsound.ui.home.postList.BoardPostListPresenterFactory;

import dagger.Subcomponent;

@SingleIn(HomeScreenComponent.class)
@Subcomponent (modules = HomeScreenModule.class)
public interface HomeScreenComponent {

    void inject(HomeScreenActivity homeScreenActivity);

    BoardPostListPresenterFactory providePresenterFactory();
}
