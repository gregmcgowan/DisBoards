package com.drownedinsound.core;

import com.drownedinsound.ui.post.BoardPostComponent;
import com.drownedinsound.ui.postList.BoardPostListFragmentComponent;
import com.drownedinsound.ui.postList.BoardPostListParentComponent;
import com.drownedinsound.ui.postList.HomeScreenComponent;
import com.drownedinsound.ui.postList.HomeScreenModule;

/**
 * Created by gregmcgowan on 04/05/2016.
 */
public interface BaseSessionComponet {

    HomeScreenComponent homeScreenComponent(HomeScreenModule homeScreenModule);

    BoardPostListParentComponent boardPostListParentComponent();

    BoardPostListFragmentComponent boardPostListComponent();

    BoardPostComponent boardPostComponent();
}
