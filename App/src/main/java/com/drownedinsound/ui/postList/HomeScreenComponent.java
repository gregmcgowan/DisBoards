package com.drownedinsound.ui.postList;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 08/01/2017.
 */

@SingleIn(HomeScreenComponent.class)
@Subcomponent (modules = HomeScreenModule.class)
public interface HomeScreenComponent {

    void inject(HomeScreenActivity homeScreenActivity);

    void inject(HomeScreenAdapter homeScreenAdapter);

    BoardPostListPresenterFactory providePresenterFactory();
}
