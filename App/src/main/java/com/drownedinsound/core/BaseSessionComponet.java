package com.drownedinsound.core;

import com.drownedinsound.ui.post.BoardPostComponent;
import com.drownedinsound.ui.home.HomeScreenComponent;
import com.drownedinsound.ui.home.HomeScreenModule;
import com.drownedinsound.ui.post.BoardPostModule;


public interface BaseSessionComponet {

    HomeScreenComponent homeScreenComponent(HomeScreenModule homeScreenModule);

    BoardPostComponent boardPostComponent(BoardPostModule boardPostModule);
}
