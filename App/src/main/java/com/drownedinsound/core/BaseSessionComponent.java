package com.drownedinsound.core;

import com.drownedinsound.ui.post.BoardPostComponent;
import com.drownedinsound.ui.home.HomeScreenComponent;

interface BaseSessionComponent {

    HomeScreenComponent.Builder provideHomeScreenBuilder();
    BoardPostComponent.Builder provideBoardPostBuilder();
}
